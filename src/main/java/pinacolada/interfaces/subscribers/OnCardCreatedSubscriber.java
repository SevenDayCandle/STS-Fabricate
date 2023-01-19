package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCardCreatedSubscriber extends PCLCombatSubscriber
{
    void onCardCreated(AbstractCard card, boolean startOfBattle);
}
