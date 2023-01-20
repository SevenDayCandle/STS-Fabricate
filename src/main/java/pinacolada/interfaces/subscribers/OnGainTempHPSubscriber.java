package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnGainTempHPSubscriber extends PCLCombatSubscriber
{
    int onGainTempHP(int amount);
}
