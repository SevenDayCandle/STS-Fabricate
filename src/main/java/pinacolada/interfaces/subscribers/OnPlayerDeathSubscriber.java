package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnPlayerDeathSubscriber extends PCLCombatSubscriber
{
    void onAfterDeath();
}