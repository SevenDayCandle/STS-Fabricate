package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCardResetSubscriber
{
    void onCardReset(AbstractCard card);
}