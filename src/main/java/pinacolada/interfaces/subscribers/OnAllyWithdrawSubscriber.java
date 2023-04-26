package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

@CombatSubscriber
public interface OnAllyWithdrawSubscriber extends PCLCombatSubscriber {
    void onAllyWithdraw(PCLCard returned, PCLCardAlly ally);
}
