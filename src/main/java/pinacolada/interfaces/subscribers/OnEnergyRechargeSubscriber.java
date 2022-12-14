package pinacolada.interfaces.subscribers;

import org.apache.commons.lang3.mutable.MutableInt;

public interface OnEnergyRechargeSubscriber
{
    void onEnergyRecharge(MutableInt previousEnergy, MutableInt currentEnergy);
} 