package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;

@CombatSubscriber
public interface OnAllyTriggerSubscriber extends PCLCombatSubscriber {
    void onAllyTrigger(PCLCard card, AbstractCreature target, PCLCardAlly ally, PCLCardAlly caller);
}
