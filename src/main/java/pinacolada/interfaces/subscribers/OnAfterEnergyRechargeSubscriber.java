package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnAfterEnergyRechargeSubscriber extends PCLCombatSubscriber
{
    void onAfterEnergyRecharge();
}
