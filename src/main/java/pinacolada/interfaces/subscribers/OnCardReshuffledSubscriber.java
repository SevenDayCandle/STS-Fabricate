package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardReshuffledSubscriber extends PCLCombatSubscriber
{
    void onCardReshuffled(AbstractCard card, CardGroup sourcePile);
}
