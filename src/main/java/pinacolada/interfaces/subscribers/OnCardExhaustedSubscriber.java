package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardExhaustedSubscriber extends PCLCombatSubscriber {
    void onCardExhausted(AbstractCard card);
}