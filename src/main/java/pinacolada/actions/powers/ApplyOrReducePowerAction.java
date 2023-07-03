package pinacolada.actions.powers;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.utility.NestedAction;
import pinacolada.patches.actions.ApplyPowerActionPatches;
import pinacolada.powers.PCLPowerHelper;

public class ApplyOrReducePowerAction extends NestedAction<AbstractPower> {
    public AbstractPower power;
    public boolean ignoreArtifact;
    public boolean showEffect = true;
    public boolean allowNegative = false;
    public boolean canStack = true;
    public boolean skipIfZero = true;

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, PCLPowerHelper power) {
        this(source, target, power, 1);
    }

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, PCLPowerHelper power, int amount) {
        this(source, target, power.create(target, source, amount));
    }

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, PCLPowerHelper power, int amount, boolean temporary) {
        this(source, target, power.create(target, source, amount, temporary));
    }

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, AbstractPower power) {
        this(source, target, power, power.amount);
    }

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, AbstractPower power, int amount) {
        super(ActionType.POWER, 0f);
        this.power = power;
        initialize(source, target, amount);
    }

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, AbstractPower power, int amount, AttackEffect effect) {
        this(source, target, power, power.amount);
        this.attackEffect = effect;
    }

    public ApplyOrReducePowerAction allowNegative(boolean skipIfNegative) {
        this.allowNegative = skipIfNegative;

        return this;
    }

    public ApplyOrReducePowerAction canStack(boolean canStack) {
        this.canStack = canStack;

        return this;
    }

    protected AbstractPower extractPower() {
        if (action instanceof ApplyPowerAction) {
            return ReflectionHacks.getPrivate(action, ApplyPowerAction.class, "powerToApply");
        }
        else if (action instanceof ReducePowerAction) {
            return ReflectionHacks.getPrivate(action, ReducePowerAction.class, "powerInstance");
        }
        return null;
    }

    @Override
    protected void firstUpdate() {
        if (shouldCancelAction() || (amount == 0 && skipIfZero)) {
            complete(null);
            return;
        }

        // Powers that can go negative (like Strength) should always use Apply so that characters without Strength can gain negative amounts of it
        if (amount >= 0 || power.canGoNegative || allowNegative) {
            action = new ApplyPowerAction(target, source, power, amount, Settings.FAST_MODE, attackEffect);
            if (ignoreArtifact) {
                ApplyPowerActionPatches.IgnoreArtifact.ignoreArtifact.set(action, true);
            }
        }
        // INVERT amount because reduce power expects a positive amount to remove
        else {
            action = new ReducePowerAction(target, source, power.ID, -amount);
        }
    }

    public ApplyOrReducePowerAction ignoreArtifact(boolean ignoreArtifact) {
        this.ignoreArtifact = ignoreArtifact;

        return this;
    }

    @Override
    protected void onNestCompleted() {
        // TODO check if the power was actually successfully applied
        complete(extractPower());
    }

    public ApplyOrReducePowerAction skipIfZero(boolean skipIfZero) {
        this.skipIfZero = skipIfZero;

        return this;
    }
}
