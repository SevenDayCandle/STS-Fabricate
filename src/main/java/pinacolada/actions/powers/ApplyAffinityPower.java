package pinacolada.actions.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.PowerBuffEffect;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.effects.PCLEffects;
import pinacolada.powers.PCLAffinityPower;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class ApplyAffinityPower extends PCLActionWithCallback<AbstractPower>
{
    public PCLAffinityPower power;
    public boolean showEffect;
    public boolean pay;
    public boolean temporary;

    public ApplyAffinityPower(AbstractCreature source, PCLAffinity affinity, int amount)
    {
        this(source, affinity, amount, false);
    }

    public ApplyAffinityPower(AbstractCreature source, PCLAffinity affinity, int amount, boolean temporary)
    {
        super(ActionType.POWER, Settings.ACTION_DUR_XFAST);

        this.temporary = temporary;
        this.power = GameUtilities.getPCLAffinityPower(affinity);

        if (power == null || AbstractDungeon.getMonsters().areMonstersBasicallyDead())
        {
            complete();
            return;
        }

        initialize(source, power.owner, amount);
    }

    @Override
    protected void firstUpdate()
    {
        if (amount == 0)
        {
            complete();
            return;
        }

        if (shouldCancelAction() || !GameUtilities.canApplyPower(source, target, power, this))
        {
            complete(power);
            return;
        }

        if (source != null)
        {
            for (AbstractPower power : source.powers)
            {
                power.onApplyPower(this.power, target, source);
            }
        }

        power.stack(amount, pay, temporary);
        power.flash();

        if (showEffect)
        {
            PCLEffects.List.add(new PowerBuffEffect(target.hb.cX - target.animX, target.hb.cY + target.hb.height / 2f, "+" + amount + " " + power.name));
        }

        AbstractDungeon.onModifyPower();
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            complete(power);
        }
    }

    public ApplyAffinityPower setPay(boolean pay)
    {
        this.pay = pay;

        return this;
    }

    public ApplyAffinityPower setTemporary(boolean temporary)
    {
        this.temporary = temporary;

        return this;
    }

    public ApplyAffinityPower showEffect(boolean showEffect)
    {
        this.showEffect = showEffect;

        return this;
    }
}
