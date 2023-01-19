package pinacolada.interfaces.subscribers;

import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

public interface OnAllyWithdrawSubscriber extends PCLCombatSubscriber
{
    void onAllyWithdraw(PCLCard returned, PCLCardAlly ally);
}
