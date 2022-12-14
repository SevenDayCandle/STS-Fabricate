package pinacolada.interfaces.subscribers;

import pinacolada.misc.CombatStats;

public interface OnGainPowerBonusSubscriber
{
    float onGainPowerBonus(String powerID, CombatStats.Type gainType, float amount);
}
