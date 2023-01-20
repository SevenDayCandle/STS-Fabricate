package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnAfterDeathSubscriber extends PCLCombatSubscriber
{
    void onAfterDeath();
}