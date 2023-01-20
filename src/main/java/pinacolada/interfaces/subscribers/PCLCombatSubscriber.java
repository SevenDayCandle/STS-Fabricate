package pinacolada.interfaces.subscribers;

import pinacolada.misc.CombatManager;

public interface PCLCombatSubscriber
{
    default void subscribeToAll()
    {
        CombatManager.subscribe(this);
    }

    default void unsubscribeFromAll()
    {
        CombatManager.unsubscribe(this);
    }
}
