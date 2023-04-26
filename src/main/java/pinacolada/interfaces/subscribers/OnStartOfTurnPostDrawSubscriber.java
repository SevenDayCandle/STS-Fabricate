package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnStartOfTurnPostDrawSubscriber extends PCLCombatSubscriber {
    void onStartOfTurnPostDraw();
}