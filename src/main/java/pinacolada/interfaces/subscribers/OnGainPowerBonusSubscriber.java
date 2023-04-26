package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;
import pinacolada.dungeon.CombatManager;

@CombatSubscriber
public interface OnGainPowerBonusSubscriber extends PCLCombatSubscriber {
    float onGainPowerBonus(String powerID, CombatManager.Type gainType, float amount);
}
