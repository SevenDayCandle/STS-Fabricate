package pinacolada.interfaces.subscribers;

import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

public interface OnAllyWithdrawSubscriber
{
    void onAllyWithdraw(PCLCardAlly ally, PCLCard returned);
}
