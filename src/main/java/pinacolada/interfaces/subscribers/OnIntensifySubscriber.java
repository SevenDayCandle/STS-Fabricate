package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.PCLAffinity;

@CombatSubscriber
public interface OnIntensifySubscriber extends PCLCombatSubscriber
{
    void onIntensify(PCLAffinity button);
}