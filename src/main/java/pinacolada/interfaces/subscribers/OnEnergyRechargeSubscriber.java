package pinacolada.interfaces.subscribers;

public interface OnEnergyRechargeSubscriber extends PCLCombatSubscriber
{
    int onEnergyRecharge(int originalEnergy, int newEnergy);
} 