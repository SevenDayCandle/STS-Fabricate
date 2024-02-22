package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCreatureDeathSubscriber extends PCLCombatSubscriber {
    boolean onDeath(AbstractCreature monster, boolean triggerRelics);
}
