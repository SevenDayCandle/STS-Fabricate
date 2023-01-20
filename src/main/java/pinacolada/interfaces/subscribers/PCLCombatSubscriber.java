package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;
import pinacolada.misc.CombatManager;

/** ALL interfaces that extend PCLCombatSubscriber will automatically have events created for them in the combat manager */
@CombatSubscriber
public interface PCLCombatSubscriber
{
    default void subscribeToAll()
    {
        CombatManager.subscribe(this);
    }

    default void subscribeToAllOnce()
    {
        CombatManager.subscribeOnce(this);
    }

    default void unsubscribeFromAll()
    {
        CombatManager.unsubscribe(this);
    }
}
