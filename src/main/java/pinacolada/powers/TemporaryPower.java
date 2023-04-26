package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

public class TemporaryPower extends PCLPower {
    public static final String ID = createFullID(TemporaryPower.class);

    private final AbstractPower power;
    public int stabilizeTurns;
    private int sourceMaxAmount = Integer.MAX_VALUE;

    public TemporaryPower(AbstractCreature owner, AbstractPower sourcePower) {
        super(owner, ID + sourcePower.ID);
        power = sourcePower;
        this.img = sourcePower.img;
        this.amount = sourcePower.amount;
        this.region48 = sourcePower.region128;
        this.powerStrings = CardCrawlGame.languagePack.getPowerStrings(ID);
        if (sourcePower instanceof PCLPower) {
            this.sourceMaxAmount = ((PCLPower) sourcePower).maxAmount;
        }
        this.canGoNegative = true;
        initialize(amount, sourcePower.type, true);

        this.name = formatDescription(2, sourcePower.name);
        updateDescription();
    }

    public static TemporaryPower getFromCreature(AbstractCreature owner, String sourcePowerID) {
        for (AbstractPower po : owner.powers) {
            TemporaryPower tmp = EUIUtils.safeCast(po, TemporaryPower.class);
            if (tmp != null && sourcePowerID.equals(tmp.power.ID)) {
                return tmp;
            }
        }
        return null;
    }

    @Override
    public String getUpdatedDescription() {
        if (power == null) {
            return super.getUpdatedDescription();
        }
        return amount < 0 ? formatDescription(1, -amount, power.name) : formatDescription(0, amount, power.name);
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference) {
        PCLActions.top.applyPower(owner, owner, power, difference).ignoreArtifact(true).addCallback(this::tryRemoveSourcePower);

        // The type should be set to Neutral after the initial stacking occurs (i.e. after artifact checks) so that this power will not influence buff/debuff count checks
        type = NeutralPowertypePatch.NEUTRAL;

        super.onAmountChanged(previousAmount, difference);
    }

    @Override
    public void updateDescription() {
        super.updateDescription();
        mainTip.title = name;
    }

    @Override
    public void stackPower(int stackAmount, boolean updateBaseAmount) {
        int sourceAmount = GameUtilities.getPowerAmount(owner, power.ID);
        if (updateBaseAmount && (baseAmount += stackAmount) > maxAmount) {
            baseAmount = maxAmount;
        }
        if ((sourceAmount + stackAmount) > sourceMaxAmount) {
            stackAmount = sourceMaxAmount - sourceAmount;
        }
        if ((amount + stackAmount) > maxAmount) {
            stackAmount = maxAmount - amount;
        }

        final int previous = amount;
        this.fontScale = 8.0F;
        this.amount += stackAmount;

        onAmountChanged(previous, stackAmount);
    }

    @Override
    protected void renderIconsImpl(SpriteBatch sb, float x, float y, Color borderColor, Color imageColor) {
        PCLRenderHelpers.drawSepia(sb, (s) ->
                super.renderIconsImpl(s, x, y, borderColor, imageColor)
        );
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

    protected void tryRemoveSourcePower() {
        int powerAmount = GameUtilities.getPowerAmount(owner, power.ID);
        if (amountBelowThreshold(powerAmount)) {
            PCLActions.bottom.removePower(owner, owner, power.ID);
        }
    }

    protected boolean amountBelowThreshold(int powerAmount) {
        return (powerAmount < 0 && !power.canGoNegative) || (powerAmount == 0);
    }

    public void stabilize(int turns) {
        stabilizeTurns += turns;
    }
}