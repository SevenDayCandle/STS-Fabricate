package pinacolada.interfaces.subscribers;

import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

public interface OnAllyTriggerSubscriber extends PCLCombatSubscriber
{
    void onAllyTrigger(PCLCard card, PCLCardAlly ally);
}
