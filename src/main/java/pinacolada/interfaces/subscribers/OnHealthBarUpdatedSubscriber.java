package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnHealthBarUpdatedSubscriber extends PCLCombatSubscriber {
    void onHealthBarUpdated(AbstractCreature creature);
}
