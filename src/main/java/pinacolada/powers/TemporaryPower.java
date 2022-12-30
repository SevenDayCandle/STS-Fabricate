package pinacolada.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import pinacolada.actions.PCLActions;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.Constructor;

public class TemporaryPower extends PCLPower
{
    public static final String ID = createFullID(TemporaryPower.class);

    private final boolean sourcePowerCanBeNegative;
    private final String targetName;
    private final FuncT1<AbstractPower, AbstractCreature> constructorT1;
    private final FuncT2<AbstractPower, AbstractCreature, Integer> constructorT2;
    private int sourceMaxAmount = 9999;
    public final String sourcePowerID;
    public int stabilizeTurns;

    public static TemporaryPower getFromCreature(AbstractCreature owner, String sourcePowerID)
    {
        for (AbstractPower po : owner.powers)
        {
            TemporaryPower tmp = EUIUtils.safeCast(po, TemporaryPower.class);
            if (tmp != null && sourcePowerID.equals(tmp.sourcePowerID))
            {
                return tmp;
            }
        }
        return null;
    }

    public TemporaryPower(AbstractCreature owner, AbstractPower sourcePower)
    {
        super(owner, ID + sourcePower.ID);
        Constructor<? extends AbstractPower> constructor = EUIUtils.tryGetConstructor(sourcePower.getClass(), AbstractCreature.class);
        if (constructor != null)
        {
            this.constructorT1 = (c) -> {
                try
                {
                    return constructor.newInstance(c);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            };
        }
        else
        {
            this.constructorT1 = null;
        }
        Constructor<? extends AbstractPower> constructor2 = EUIUtils.tryGetConstructor(sourcePower.getClass(), AbstractCreature.class, int.class);
        if (constructor2 != null)
        {
            this.constructorT2 = (c, i) -> {
                try
                {
                    return constructor2.newInstance(c, i);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            };
        }
        else
        {
            this.constructorT2 = null;
        }
        this.sourcePowerCanBeNegative = sourcePower.canGoNegative;
        this.sourcePowerID = sourcePower.ID;
        this.targetName = sourcePower.name;
        this.img = sourcePower.img;
        this.amount = sourcePower.amount;
        this.region48 = sourcePower.region128;
        this.useTemporaryColoring = true;
        this.powerStrings = CardCrawlGame.languagePack.getPowerStrings(ID);
        if (sourcePower instanceof PCLPower)
        {
            this.sourceMaxAmount = ((PCLPower) sourcePower).maxAmount;
        }
        initialize(amount, sourcePower.type, true);

        this.name = formatDescription(2, targetName);
        updateDescription();
    }

    @Override
    public String getUpdatedDescription()
    {
        return amount < 0 ? formatDescription(1, -amount, targetName) : formatDescription(0, amount, targetName);
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
            if (amount < 0 && constructorT2 != null)
            {
                PCLActions.top.applyPower(constructorT2.invoke(owner, -amount)).ignoreArtifact(true);
            }
            removePower();
        }
    }

    @Override
    public void stackPower(int stackAmount, boolean updateBaseAmount)
    {
        int sourceAmount = GameUtilities.getPowerAmount(owner, sourcePowerID);
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
        if (constructorT2 != null)
        {
            PCLActions.top.applyPower(constructorT2.invoke(owner, difference)).ignoreArtifact(true).addCallback(this::removeSourcePower);
        }
        else if (constructorT1 != null && previousAmount + difference != 0)
        {
            PCLActions.top.applyPower(constructorT1.invoke(owner)).ignoreArtifact(true).addCallback(this::removeSourcePower);
        }

        super.onAmountChanged(previousAmount, difference);
    }

    protected void removeSourcePower()
    {
        if (GameUtilities.getPowerAmount(sourcePowerID) <= 0 && !sourcePowerCanBeNegative)
        {
            PCLActions.bottom.removePower(owner, owner, sourcePowerID);
        }
    }

    public void stabilize(int turns)
    {
        stabilizeTurns += turns;
    }
}