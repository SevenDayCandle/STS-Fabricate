package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.PCLAffinity;

@CombatSubscriber
public interface OnTryElementReactSubscriber extends PCLCombatSubscriber
{
    int onTryElementReact(int amount, PCLAffinity button, PCLAffinity trigger);
}