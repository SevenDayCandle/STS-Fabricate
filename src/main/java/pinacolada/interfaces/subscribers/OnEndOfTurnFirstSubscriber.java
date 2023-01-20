package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnEndOfTurnFirstSubscriber extends PCLCombatSubscriber
{
    void onEndOfTurnFirst(boolean isPlayer);
}