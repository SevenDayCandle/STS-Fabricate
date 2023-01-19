package pinacolada.interfaces.subscribers;

import pinacolada.misc.CombatManager;

/** ALL interfaces that extend PCLCombatSubscriber will automatically have events created for them in the combat manager */
public interface PCLCombatSubscriber
{
    default void subscribeToAll()
    {
        CombatManager.subscribe(this);
    }
}
