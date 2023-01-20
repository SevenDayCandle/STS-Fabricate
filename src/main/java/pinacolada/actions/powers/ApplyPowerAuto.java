//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package pinacolada.actions.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActionAutoTarget;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.powers.PCLPowerHelper;

// Copied and modified from STS-AnimatorMod
public class ApplyPowerAuto extends PCLActionAutoTarget<AbstractPower>
{
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

    public ApplyPowerAuto(AbstractCreature source, AbstractCreature target, PCLCardTarget targetHelper, PCLPowerHelper powerHelper, int amount)
    {
        this(source, target, targetHelper, powerHelper, amount, 1);
    }

    public ApplyPowerAuto(AbstractCreature source, AbstractCreature target, PCLCardTarget targetHelper, PCLPowerHelper powerHelper, int amount, int limit)
    {
        super(ActionType.POWER);

        this.powerHelper = powerHelper;

        initialize(source, target, targetHelper, amount, limit);

        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() || this.powerHelper == null)
        {
            complete();
        }
    }

    public ApplyPowerAuto canStack(boolean canStack)
    {
        this.canStack = canStack;

        return this;
    }

    @Override
    protected void firstUpdate()
    {
        for (AbstractCreature target : findTargets(true)) // Reverse because of GameActions.Top
        {
            ApplyPower action = new ApplyPower(source, target, powerHelper.create(target, source, amount, temporary), amount);
            action.ignoreArtifact(ignoreArtifact);
            action.setRealtime(isRealtime);
            action.showEffect(showEffect, faster);
            action.skipIfZero(skipIfZero);
            action.skipIfNull(skipIfNull);
            action.canStack(canStack);
            action.isCancellable(canCancel);
            action.allowNegative(allowNegative);

            PCLActions.top.add(action).addCallback((ActionT1<AbstractPower>) this::complete);
        }

        complete();
    }

    public ApplyPowerAuto ignoreArtifact(boolean ignoreArtifact)
    {
        this.ignoreArtifact = ignoreArtifact;

        return this;
    }

    public ApplyPowerAuto setTemporary(boolean temporary)
    {
        this.temporary = temporary;

        return this;
    }

    public ApplyPowerAuto showEffect(boolean showEffect, boolean isFast)
    {
        this.showEffect = showEffect;
        this.faster = isFast;

        return this;
    }

    public ApplyPowerAuto skipIfNull(boolean skipIfNull)
    {
        this.skipIfNull = skipIfNull;

        return this;
    }

    public ApplyPowerAuto skipIfZero(boolean skipIfZero)
    {
        this.skipIfZero = skipIfZero;

        return this;
    }

    public ApplyPowerAuto allowNegative(boolean allowNegative)
    {
        this.allowNegative = allowNegative;

        return this;
    }
}
