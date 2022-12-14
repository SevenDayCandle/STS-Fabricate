package pinacolada.powers.common;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.misc.CombatStats;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.PCLRenderHelpers;

public class InvigoratedPower extends PCLPower implements MultiplicativePower
{
    public static final String POWER_ID = createFullID(InvigoratedPower.class);
    public static final int MULTIPLIER = 25;

    public InvigoratedPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        initialize(amount, PowerType.BUFF, true);
    }

    public static float calculateDamage(float damage, float multiplier)
    {
        return Math.max(0, damage + Math.max(0, damage * (multiplier / 100f)));
    }

    public static float getMultiplier()
    {
        return (MULTIPLIER + CombatStats.getPlayerEffectBonus(POWER_ID));
    }

    @Override
    public String getUpdatedDescription()
    {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getMultiplier()), amount, amount == 1 ? powerStrings.DESCRIPTIONS[1] : powerStrings.DESCRIPTIONS[2]);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type)
    {
        return type == DamageInfo.DamageType.NORMAL ? calculateDamage(damage, getMultiplier()) : damage;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer)
    {
        super.atEndOfTurn(isPlayer);

        reducePower(1);
    }
}
