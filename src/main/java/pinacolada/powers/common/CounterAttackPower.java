package pinacolada.powers.common;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.effects.AttackEffects;
import pinacolada.effects.SFX;
import pinacolada.powers.PCLPower;
import pinacolada.actions.PCLActions;

public class CounterAttackPower extends PCLPower
{
    public static final String POWER_ID = createFullID(CounterAttackPower.class);
    public static final int VULNERABLE_AMOUNT = 1;
    public static boolean retain = false;

    public CounterAttackPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        initialize(amount, PowerType.BUFF, false);
    }

    @Override
    public void atStartOfTurn()
    {
        super.atStartOfTurn();

        if (!retain)
        {
            removePower();
        }

    }

    @Override
    public void playApplyPowerSfx()
    {
        SFX.play(SFX.ATTACK_IRON_1, 1.25f, 1.35f, 0.7f);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount)
    {
        if (info.type == DamageInfo.DamageType.NORMAL && damageAmount < info.output)
        {
            int[] damageMatrix = DamageInfo.createDamageMatrix(amount, false);
            PCLActions.bottom.dealDamageToAll(damageMatrix, DamageInfo.DamageType.NORMAL, AttackEffects.BLUNT_HEAVY);
            reducePower(1);
        }

        return super.onAttacked(info, damageAmount);
    }
}