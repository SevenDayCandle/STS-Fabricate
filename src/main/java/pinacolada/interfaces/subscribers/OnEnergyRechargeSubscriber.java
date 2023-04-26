package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnEnergyRechargeSubscriber extends PCLCombatSubscriber {
    int onEnergyRecharge(int originalEnergy, int newEnergy);
} 