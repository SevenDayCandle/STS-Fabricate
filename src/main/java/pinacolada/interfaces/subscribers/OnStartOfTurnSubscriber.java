package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnStartOfTurnSubscriber extends PCLCombatSubscriber
{
    void onStartOfTurn();
}