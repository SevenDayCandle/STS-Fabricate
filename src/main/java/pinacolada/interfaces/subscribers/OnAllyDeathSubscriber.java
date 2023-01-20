package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

@CombatSubscriber
public interface OnAllyDeathSubscriber extends PCLCombatSubscriber
{
    void onAllyDeath(PCLCard returned, PCLCardAlly ally);
}
