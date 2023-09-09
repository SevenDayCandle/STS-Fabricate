package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

public class TemporaryPower extends PCLPower {
    public static final PCLPowerData DATA = new PCLPowerData(TemporaryPower.class, PGR.core).setEndTurnBehavior(PCLPowerData.Behavior.Special); // Do not register

    private final AbstractPower originalPower;
    private int sourceMaxAmount = Integer.MAX_VALUE;
    public int stabilizeTurns;

    public TemporaryPower(AbstractCreature owner, AbstractPower sourcePower) {
        super(DATA, owner, owner, sourcePower.amount);
        originalPower = sourcePower;
        this.ID = DATA.ID + originalPower.ID;
        this.img = sourcePower.img;
        this.region128 = this.region48 = sourcePower.region128 != null ? sourcePower.region128 : sourcePower.region48;
        if (sourcePower instanceof PCLPower) {
            this.sourceMaxAmount = ((PCLPower) sourcePower).data.maxAmount;
        }
        this.canGoNegative = true;
        this.type = sourcePower.type;
        this.name = formatDescription(2, sourcePower.name);
        mainTip.icon = this.region128 != null ? this.region128 : img != null ? new TextureRegion(img) : null;
        updateDescription();
    }

    public static TemporaryPower getFromCreature(AbstractCreature owner, String sourcePowerID) {
        for (AbstractPower po : owner.powers) {
            TemporaryPower tmp = EUIUtils.safeCast(po, TemporaryPower.class);
            if (tmp != null && sourcePowerID.equals(tmp.originalPower.ID)) {
                return tmp;
            }
        }
        return null;
    }

    protected boolean amountBelowThreshold(int powerAmount) {
        return (powerAmount < 0 && !originalPower.canGoNegative) || (powerAmount == 0);
    }

    @Override
    public void atStartOfTurn() {
        if (stabilizeTurns > 0) {
            stabilizeTurns -= 1;
        }
        else {
            removePower();
        }
    }

    @Override
    public String getUpdatedDescription() {
        if (originalPower == null) {
            return formatDescription(0, amount, powerStrings.NAME);
        }
        return amount < 0 ? formatDescription(1, -amount, originalPower.name) : formatDescription(0, amount, originalPower.name);
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference) {
        PCLActions.top.applyPower(owner, owner, originalPower, difference).ignoreArtifact(true).addCallback(this::tryRemoveSourcePower);

        // The type should be set to Neutral after the initial stacking occurs (i.e. after artifact checks) so that this power will not influence buff/debuff count checks
        type = NeutralPowertypePatch.NEUTRAL;

        super.onAmountChanged(previousAmount, difference);
    }

    @Override
    protected void renderIconsImpl(SpriteBatch sb, float x, float y, Color borderColor, Color imageColor) {
        PCLRenderHelpers.drawSepia(sb, (s) ->
                super.renderIconsImpl(s, x, y, borderColor, imageColor)
        );
    }

    public void stabilize(int turns) {
        stabilizeTurns += turns;
    }

    @Override
    public void stackPower(int stackAmount, boolean updateBaseAmount) {
        int sourceAmount = GameUtilities.getPowerAmount(owner, originalPower.ID);
        if (updateBaseAmount && (baseAmount += stackAmount) > sourceMaxAmount) {
            baseAmount = sourceMaxAmount;
        }
        if ((sourceAmount + stackAmount) > sourceMaxAmount) {
            stackAmount = sourceMaxAmount - sourceAmount;
        }
        if ((amount + stackAmount) > sourceMaxAmount) {
            stackAmount = sourceMaxAmount - amount;
        }

        final int previous = amount;
        this.fontScale = 8.0F;
        this.amount += stackAmount;

        onAmountChanged(previous, stackAmount);
    }

    protected void tryRemoveSourcePower() {
        int powerAmount = GameUtilities.getPowerAmount(owner, originalPower.ID);
        if (amountBelowThreshold(powerAmount)) {
            PCLActions.bottom.removePower(owner, owner, originalPower.ID);
        }
    }

    @Override
    public void updateDescription() {
        super.updateDescription();
        mainTip.title = name;
    }
}