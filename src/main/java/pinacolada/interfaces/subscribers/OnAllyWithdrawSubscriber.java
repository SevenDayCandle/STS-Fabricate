package pinacolada.interfaces.subscribers;

import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

public interface OnAllyWithdrawSubscriber
{
    void onAllyWithdraw(PCLCard returned, PCLCardAlly ally);
}
