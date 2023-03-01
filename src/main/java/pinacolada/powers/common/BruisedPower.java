package pinacolada.powers.common;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.powers.PCLPower;

public class BruisedPower extends PCLPower
{
    public static final String POWER_ID = createFullID(BruisedPower.class);

    public BruisedPower(AbstractCreature owner, AbstractCreature source, int amount)
    {
        super(owner, source, POWER_ID);
        initialize(amount, PowerType.DEBUFF, true);
    }

    @Override
    public void atStartOfTurn()
    {
        super.atStartOfTurn();
        reducePower(1);
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type)
    {
        return super.atDamageReceive(type == DamageInfo.DamageType.NORMAL ? damage + amount : damage, type);
    }
}
