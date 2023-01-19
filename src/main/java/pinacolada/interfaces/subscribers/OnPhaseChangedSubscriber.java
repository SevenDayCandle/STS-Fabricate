package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.actions.GameActionManager;

public interface OnPhaseChangedSubscriber extends PCLCombatSubscriber
{
    void onPhaseChanged(GameActionManager.Phase phase);
}