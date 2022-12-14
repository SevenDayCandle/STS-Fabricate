package pinacolada.interfaces.subscribers;

import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

public interface OnAllySummonSubscriber
{
    void onAllySummon(PCLCard card, PCLCardAlly ally);
}
