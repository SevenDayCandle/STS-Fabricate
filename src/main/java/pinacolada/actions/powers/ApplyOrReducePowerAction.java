package pinacolada.actions.powers;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.utility.NestedAction;
import pinacolada.patches.actions.ApplyPowerActionPatches;

public class ApplyOrReducePowerAction extends NestedAction<AbstractPower>
{
    public AbstractPower power;
    public boolean ignoreArtifact;
    public boolean showEffect = true;
    public boolean allowNegative = false;
    public boolean canStack = true;
    public boolean skipIfZero = true;

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, AbstractPower power)
    {
        this(source, target, power, power.amount);
    }

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, AbstractPower power, int amount)
    {
        super(ActionType.POWER, 0f);
        this.power = power;
        initialize(source, target, amount);
    }

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, AbstractPower power, int amount, AttackEffect effect)
    {
        this(source, target, power, power.amount);
        this.attackEffect = effect;
    }

    @Override
    protected void firstUpdate()
    {
        if (shouldCancelAction() || (amount == 0 && skipIfZero))
        {
            complete();
            return;
        }

        // Powers that can go negative (like Strength) should always use Apply so that characters without Strength can gain negative amounts of it
        if (amount >= 0 || power.canGoNegative || allowNegative)
        {
            action = new ApplyPowerAction(target, source, power, amount, Settings.FAST_MODE, attackEffect);
            if (ignoreArtifact)
            {
                ApplyPowerActionPatches.IgnoreArtifact.ignoreArtifact.set(action, true);
            }
        }
        else
        {
            action = new ReducePowerAction(target, source, power, amount);
        }
    }

    protected AbstractPower extractPower()
    {
        if (action instanceof ApplyPowerAction)
        {
            return ReflectionHacks.getPrivate(action, ApplyPowerAction.class, "powerToApply");
        }
        else if (action instanceof ReducePowerAction)
        {
            return ReflectionHacks.getPrivate(action, ReducePowerAction.class, "powerInstance");
        }
        return null;
    }

    @Override
    protected void onNestCompleted()
    {
        // TODO check if the power was actually successfully applied
        complete(extractPower());
    }

    public ApplyOrReducePowerAction ignoreArtifact(boolean ignoreArtifact)
    {
        this.ignoreArtifact = ignoreArtifact;

        return this;
    }

    public ApplyOrReducePowerAction allowNegative(boolean skipIfNegative)
    {
        this.allowNegative = skipIfNegative;

        return this;
    }

    public ApplyOrReducePowerAction canStack(boolean canStack)
    {
        this.canStack = canStack;

        return this;
    }

    public ApplyOrReducePowerAction skipIfZero(boolean skipIfZero)
    {
        this.skipIfZero = skipIfZero;

        return this;
    }
}