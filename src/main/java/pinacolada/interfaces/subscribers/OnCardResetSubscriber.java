package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCardResetSubscriber extends PCLCombatSubscriber
{
    void onCardReset(AbstractCard card);
}