package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardDiscardedSubscriber extends PCLCombatSubscriber {
    void onCardDiscarded(AbstractCard card);
}
