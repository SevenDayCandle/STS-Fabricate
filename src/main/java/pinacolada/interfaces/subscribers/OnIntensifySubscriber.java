package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.fields.PCLAffinity;

@CombatSubscriber
public interface OnIntensifySubscriber extends PCLCombatSubscriber {
    void onIntensify(PCLAffinity button);
}