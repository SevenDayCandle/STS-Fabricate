package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.actions.GameActionManager;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnPhaseChangedSubscriber extends PCLCombatSubscriber {
    void onPhaseChanged(GameActionManager.Phase phase);
}