package pinacolada.interfaces.markers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.skills.skills.PTrigger;

public interface TriggerConnection {
    default void onActivate() {
    }

    boolean canActivate(PTrigger trigger);

    AbstractCreature getOwner();
}
