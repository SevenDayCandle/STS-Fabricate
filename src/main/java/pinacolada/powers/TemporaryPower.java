package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

public class TemporaryPower extends PCLPower
{
    public static final String ID = createFullID(TemporaryPower.class);

    private final AbstractPower power;
    private int sourceMaxAmount = 9999;
    public int stabilizeTurns;

    public static TemporaryPower getFromCreature(AbstractCreature owner, String sourcePowerID)
    {
        for (AbstractPower po : owner.powers)
        {
            TemporaryPower tmp = EUIUtils.safeCast(po, TemporaryPower.class);
            if (tmp != null && sourcePowerID.equals(tmp.power.ID))
            {
                return tmp;
            }
        }
        return null;
    }

    public TemporaryPower(AbstractCreature owner, AbstractPower sourcePower)
    {
        super(owner, ID + sourcePower.ID);
        power = sourcePower;
        this.img = sourcePower.img;
        this.amount = sourcePower.amount;
        this.region48 = sourcePower.region128;
        this.powerStrings = CardCrawlGame.languagePack.getPowerStrings(ID);
        if (sourcePower instanceof PCLPower)
        {
            this.sourceMaxAmount = ((PCLPower) sourcePower).maxAmount;
        }
        initialize(amount, sourcePower.type, true);

        this.name = formatDescription(2, sourcePower.name);
        updateDescription();
    }

    @Override
    public String getUpdatedDescription()
    {
        if (power == null)
        {
            return super.getUpdatedDescription();
        }
        return amount < 0 ? formatDescription(1, -amount, power.name) : formatDescription(0, amount, power.name);
    }

    @Override
    public void updateDescription()
    {
        super.updateDescription();
        mainTip.title = name;
    }

    @Override
    public void atStartOfTurn()
    {
        if (stabilizeTurns > 0) {
            stabilizeTurns -= 1;
        }
        else
        {
            // If a power was made with constructorT2, it means that the power is amount based
            if (amount < 0)
            {
                PCLActions.top.applyPower(owner, owner, power, -amount).ignoreArtifact(true);
            }
            removePower();
        }
    }

    @Override
    public void stackPower(int stackAmount, boolean updateBaseAmount)
    {
        int sourceAmount = GameUtilities.getPowerAmount(owner, power.ID);
        if (updateBaseAmount && (baseAmount += stackAmount) > maxAmount)
        {
            baseAmount = maxAmount;
        }
        if ((sourceAmount + stackAmount) > sourceMaxAmount)
        {
            stackAmount = sourceMaxAmount - sourceAmount;
        }
        if ((amount + stackAmount) > maxAmount)
        {
            stackAmount = maxAmount - amount;
        }

        final int previous = amount;
        this.fontScale = 8.0F;
        this.amount += stackAmount;

        onAmountChanged(previous, stackAmount);
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference)
    {
        PCLActions.top.applyPower(owner, owner, power, difference).ignoreArtifact(true).addCallback(this::removeSourcePower);
        // Change the power type when this power's sign changes
        if (previousAmount < 0 ^ amount < 0)
        {
            type = getSourcePowerType();
        }
        super.onAmountChanged(previousAmount, difference);
    }

    @Override
    protected void renderIconsImpl(SpriteBatch sb, float x, float y, Color borderColor, Color imageColor)
    {
        PCLRenderHelpers.drawSepia(sb, (s) ->
                super.renderIconsImpl(s, x, y, borderColor, imageColor)
        );
    }

    protected void removeSourcePower()
    {
        int powerAmount = GameUtilities.getPowerAmount(owner, power.ID);
        if ((powerAmount < 0 && !power.canGoNegative) || (powerAmount == 0 && (!(power instanceof PCLPower) || !((PCLPower) power).canBeZero)))
        {
            PCLActions.bottom.removePower(owner, owner, power.ID);
        }
    }

    // Obtain the original power's debuff/buff status, taking inverted stacks into account
    protected PowerType getSourcePowerType()
    {
        PowerType originalType = power.type;
        if (power.canGoNegative)
        {
            // Flip the value if either
            // The power was originally negative but is no longer so
            // The power was not negative but is now so
            // However, we should be ignoring the case of 0 stacks and the case of neutral powers
            if ((power.amount < 0 && amount > 0) || (power.amount > 0 && amount < 0))
            {
                if (originalType == PowerType.BUFF)
                {
                    originalType = PowerType.DEBUFF;
                }
                else if (originalType == PowerType.DEBUFF)
                {
                    originalType = PowerType.BUFF;
                }
            }
        }
        return originalType;
    }

    public void stabilize(int turns)
    {
        stabilizeTurns += turns;
    }
}