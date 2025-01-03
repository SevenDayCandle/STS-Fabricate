package pinacolada.actions.powers;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.utility.NestedAction;
import pinacolada.patches.actions.ApplyPowerActionPatches;
import pinacolada.powers.PCLPowerData;

public class ApplyOrReducePowerAction extends NestedAction<AbstractPower> {
    public AbstractPower power;
    public boolean ignoreArtifact;
    public boolean showEffect = true;
    public boolean allowNegative = false;
    public boolean canStack = true;
    public boolean forceIfDead = false;
    public boolean skipIfZero = true;
    public boolean isRecusrive = false;

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, PCLPowerData power) {
        this(source, target, power, 1);
    }

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, PCLPowerData power, int amount) {
        this(source, target, power.create(target, source, amount));
        allowNegative = power.isNonStacking();
    }

    public ApplyOrReducePowerAction(AbstractCreature source, AbstractCreature target, PCLPowerData power, int amount, boolean temporary) {
        this(source, target, temporary ? power.createTemporary(target, source, amount) : power.create(target, source, amount));
        allowNegative = power.isNonStacking();
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

    public AbstractPower extractPower() {
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
        if ((!forceIfDead && shouldCancelAction()) || (amount == 0 && skipIfZero)) {
            complete(null);
            return;
        }

        // Powers that can go negative (like Strength) and powers that don't use amounts (like Confusion) should always use Apply so that characters without it can gain negative amounts of it
        if (amount >= 0 || power.canGoNegative || allowNegative) {
            action = new ApplyPowerAction(target, source, power, amount, Settings.FAST_MODE, attackEffect);
            if (ignoreArtifact) {
                ApplyPowerActionPatches.IgnoreArtifact.ignoreArtifact.set(action, true);
            }
            // If triggered by a custom Fabricate power that triggers on power application of the same type, mark action as recursive to avoid infinite loops
            if (isRecusrive) {
                ApplyPowerActionPatches.Recursive.recursive.set(action, true);
            }
        }
        // INVERT amount because reduce power expects a positive amount to remove
        else {
            action = new ReducePowerAction(target, source, power.ID, -amount);
            // If triggered by a custom Fabricate power that triggers on power application of the same type, mark action as recursive to avoid infinite loops
            if (isRecusrive) {
                ApplyPowerActionPatches.Recursive.recursive.set(action, true);
            }
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

    public ApplyOrReducePowerAction forceIfDead(boolean forceIfDead) {
        this.forceIfDead = forceIfDead;

        return this;
    }

    public ApplyOrReducePowerAction recursive(boolean recursive) {
        this.isRecusrive = recursive;

        return this;
    }

    public ApplyOrReducePowerAction skipIfZero(boolean skipIfZero) {
        this.skipIfZero = skipIfZero;

        return this;
    }
}
