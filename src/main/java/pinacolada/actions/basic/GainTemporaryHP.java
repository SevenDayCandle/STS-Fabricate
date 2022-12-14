package pinacolada.actions.basic;

import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.HealEffect;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.effects.PCLEffects;
import pinacolada.misc.CombatManager;

public class GainTemporaryHP extends PCLActionWithCallback<AbstractCreature>
{
    public GainTemporaryHP(AbstractCreature target, AbstractCreature source, int amount)
    {
        super(ActionType.HEAL, Settings.ACTION_DUR_FAST);

        initialize(source, target, amount);
    }

    public GainTemporaryHP(AbstractCreature target, AbstractCreature source, int amount, boolean superFast)
    {
        super(ActionType.HEAL, superFast ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        initialize(source, target, amount);
    }

    @Override
    protected void firstUpdate()
    {

        if (!target.isDying && !target.isDead)
        {
            int gainAmount = CombatManager.onGainTempHP(amount);
            TempHPField.tempHp.set(target, TempHPField.tempHp.get(target) + gainAmount);
            if (gainAmount > 0)
            {
                PCLEffects.Queue.add(new HealEffect(target.hb.cX - target.animX, target.hb.cY, gainAmount));
                target.healthBarUpdatedEvent();
            }
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            complete(target);
        }
    }
}
