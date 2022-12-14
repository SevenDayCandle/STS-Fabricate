package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.actions.GameActionManager;

public interface OnPhaseChangedSubscriber
{
    void onPhaseChanged(GameActionManager.Phase phase);
}