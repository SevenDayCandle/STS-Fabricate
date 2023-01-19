package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCardDiscardedSubscriber extends PCLCombatSubscriber
{
    void onCardDiscarded(AbstractCard card);
}
