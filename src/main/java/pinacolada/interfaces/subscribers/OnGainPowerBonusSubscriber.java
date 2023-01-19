package pinacolada.interfaces.subscribers;

import pinacolada.misc.CombatManager;

public interface OnGainPowerBonusSubscriber extends PCLCombatSubscriber
{
    float onGainPowerBonus(String powerID, CombatManager.Type gainType, float amount);
}
