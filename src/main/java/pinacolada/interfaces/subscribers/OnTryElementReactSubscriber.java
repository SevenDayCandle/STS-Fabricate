package pinacolada.interfaces.subscribers;

import pinacolada.cards.base.PCLAffinity;

public interface OnTryElementReactSubscriber
{
    int onTryElementReact(int amount, PCLAffinity button, PCLAffinity trigger);
}