package pinacolada.interfaces.subscribers;

import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

public interface OnAllyDeathSubscriber extends PCLCombatSubscriber
{
    void onAllyDeath(PCLCard returned, PCLCardAlly ally);
}
