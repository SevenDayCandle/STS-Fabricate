package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.effects.SFX;
import pinacolada.powers.PCLPower;

public class DeflectionPower extends PCLPower
{
    public static final String POWER_ID = createFullID(DeflectionPower.class);

    public DeflectionPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        initialize(amount, PowerType.BUFF, false);
    }

    @Override
    public void playApplyPowerSfx()
    {
        SFX.play(SFX.ATTACK_IRON_1, 1.25f, 1.35f, 0.7f);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount)
    {
        int blockedAmount = info.output - damageAmount;
        if (info.type == DamageInfo.DamageType.NORMAL && blockedAmount > 0 && info.owner != null)
        {
            int deflected = Math.min(amount, blockedAmount);
            PCLActions.bottom.dealDamage(owner, info.owner, deflected, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
            reducePower(deflected);
        }

        return super.onAttacked(info, damageAmount);
    }
}