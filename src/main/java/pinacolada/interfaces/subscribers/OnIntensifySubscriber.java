package pinacolada.interfaces.subscribers;

import pinacolada.cards.base.PCLAffinity;

public interface OnIntensifySubscriber extends PCLCombatSubscriber
{
    void onIntensify(PCLAffinity button);
}