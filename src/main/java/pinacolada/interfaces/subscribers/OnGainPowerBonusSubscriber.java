package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnGainPowerBonusSubscriber extends PCLCombatSubscriber {
    float onGainPowerBonus(String powerID, float amount, boolean forPlayer);
}
