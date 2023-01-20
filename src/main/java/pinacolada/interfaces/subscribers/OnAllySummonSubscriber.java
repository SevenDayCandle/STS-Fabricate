package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

@CombatSubscriber
public interface OnAllySummonSubscriber extends PCLCombatSubscriber
{
    void onAllySummon(PCLCard card, PCLCardAlly ally);
}
