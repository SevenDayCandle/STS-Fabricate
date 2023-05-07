//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package pinacolada.actions.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.PCLActionAutoTarget;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.powers.PCLPowerHelper;

// Copied and modified from STS-AnimatorMod
public class ApplyPowerAutoAction extends PCLActionAutoTarget<AbstractPower> {
    public static final String[] TEXT = ApplyPowerAction.TEXT;

    protected PCLPowerHelper powerHelper;

    protected boolean chooseRandomTarget;
    protected boolean ignoreArtifact;
    protected boolean allowNegative = false;
    protected boolean showEffect = true;
    protected boolean skipIfZero = true;
    protected boolean skipIfNull = true;
    protected boolean canStack = true;
    protected boolean temporary = false;
    protected boolean faster;

    public ApplyPowerAutoAction(AbstractCreature source, AbstractCreature target, PCLCardTarget targetHelper, PCLPowerHelper powerHelper, int amount) {
        this(source, target, targetHelper, powerHelper, amount, 1);
    }

    public ApplyPowerAutoAction(AbstractCreature source, AbstractCreature target, PCLCardTarget targetHelper, PCLPowerHelper powerHelper, int amount, int limit) {
        super(ActionType.POWER);

        this.powerHelper = powerHelper;

        initialize(source, target, targetHelper, amount, limit);

        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() || this.powerHelper == null) {
            completeImpl(); // Do not call callback
        }
    }

    public ApplyPowerAutoAction allowNegative(boolean allowNegative) {
        this.allowNegative = allowNegative;

        return this;
    }

    public ApplyPowerAutoAction canStack(boolean canStack) {
        this.canStack = canStack;

        return this;
    }

    @Override
    protected void firstUpdate() {
        for (AbstractCreature target : findTargets(true)) // Reverse because of GameActions.Top
        {
            ApplyOrReducePowerAction action = new ApplyOrReducePowerAction(source, target, powerHelper.create(target, source, amount, temporary), amount);
            action.ignoreArtifact(ignoreArtifact);
            action.setRealtime(isRealtime);
            //action.showEffect(showEffect, faster);
            action.skipIfZero(skipIfZero);
            action.canStack(canStack);
            action.isCancellable(canCancel);
            action.allowNegative(allowNegative);

            PCLActions.top.add(action).addCallback(this::complete);
        }

        completeImpl();
    }

    public ApplyPowerAutoAction ignoreArtifact(boolean ignoreArtifact) {
        this.ignoreArtifact = ignoreArtifact;

        return this;
    }

    public ApplyPowerAutoAction setTemporary(boolean temporary) {
        this.temporary = temporary;

        return this;
    }

    public ApplyPowerAutoAction showEffect(boolean showEffect, boolean isFast) {
        this.showEffect = showEffect;
        this.faster = isFast;

        return this;
    }

    public ApplyPowerAutoAction skipIfNull(boolean skipIfNull) {
        this.skipIfNull = skipIfNull;

        return this;
    }

    public ApplyPowerAutoAction skipIfZero(boolean skipIfZero) {
        this.skipIfZero = skipIfZero;

        return this;
    }
}
