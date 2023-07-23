package pinacolada.interfaces.markers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.skills.skills.PTrigger;

public interface TriggerConnection {
    boolean canActivate(PTrigger trigger);
    AbstractCreature getOwner();
    default void onActivate() {
    }
}
