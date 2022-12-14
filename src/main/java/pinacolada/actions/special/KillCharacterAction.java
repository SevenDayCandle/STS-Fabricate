package pinacolada.actions.special;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.CollectorCurseEffect;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;

public class KillCharacterAction extends PCLAction
{
    public KillCharacterAction(AbstractCreature source, AbstractCreature target)
    {
        super(AbstractGameAction.ActionType.SPECIAL, Settings.ACTION_DUR_FAST);

        initialize(source, target, 1);
    }

    @Override
    protected void firstUpdate()
    {
        PCLActions.bottom.wait(0.8f);
        PCLActions.bottom.playVFX(new CollectorCurseEffect(target.hb.cX, target.hb.cY), 2f);

        for (int i = 1; i <= 10; i ++)
        {
            PCLActions.bottom.dealDamage(source, target, i * i * i, DamageInfo.DamageType.HP_LOSS, AbstractGameAction.AttackEffect.NONE)
            .setVFX(true, false);

            PCLActions.bottom.dealDamage(source, target, i * i * i, DamageInfo.DamageType.HP_LOSS, AbstractGameAction.AttackEffect.NONE)
            .setVFX(true, false);
        }

        PCLActions.bottom.dealDamage(source, target, 99999, DamageInfo.DamageType.HP_LOSS, AbstractGameAction.AttackEffect.NONE)
        .setVFX(true, false);

        PCLActions.bottom.add(new DieAction(target));
    }
}
