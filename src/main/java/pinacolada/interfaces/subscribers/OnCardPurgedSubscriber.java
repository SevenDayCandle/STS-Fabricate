package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCardPurgedSubscriber
{
    void onPurge(AbstractCard card);
}
