package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnAfterCardDiscardedSubscriber extends PCLCombatSubscriber
{
    void onAfterCardDiscarded();
}