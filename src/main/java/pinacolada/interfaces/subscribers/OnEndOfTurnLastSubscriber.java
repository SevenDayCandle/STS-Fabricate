package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnEndOfTurnLastSubscriber extends PCLCombatSubscriber {
    void onEndOfTurnLast(boolean isPlayer);
}