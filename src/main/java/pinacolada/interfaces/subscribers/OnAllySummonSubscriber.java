package pinacolada.interfaces.subscribers;

import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

public interface OnAllySummonSubscriber extends PCLCombatSubscriber
{
    void onAllySummon(PCLCard card, PCLCardAlly ally);
}
