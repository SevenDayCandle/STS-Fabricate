package pinacolada.interfaces.subscribers;

import pinacolada.cards.base.PCLAffinity;

public interface OnTryElementReactSubscriber extends PCLCombatSubscriber
{
    int onTryElementReact(int amount, PCLAffinity button, PCLAffinity trigger);
}