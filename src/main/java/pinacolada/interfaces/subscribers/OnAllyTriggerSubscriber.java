package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

@CombatSubscriber
public interface OnAllyTriggerSubscriber extends PCLCombatSubscriber {
    void onAllyTrigger(PCLCard card, PCLCardAlly ally, PCLCardAlly caller);
}
