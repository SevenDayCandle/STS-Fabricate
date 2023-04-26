package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnShuffleSubscriber extends PCLCombatSubscriber {
    void onShuffle(boolean triggerRelics);
}
