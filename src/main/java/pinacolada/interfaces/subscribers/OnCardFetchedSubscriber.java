package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardFetchedSubscriber extends PCLCombatSubscriber {
    void onCardFetched(AbstractCard card, CardGroup sourcePile);
}
