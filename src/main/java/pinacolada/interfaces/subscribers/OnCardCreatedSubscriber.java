package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCardCreatedSubscriber
{
    void onCardCreated(AbstractCard card, boolean startOfBattle);
}
