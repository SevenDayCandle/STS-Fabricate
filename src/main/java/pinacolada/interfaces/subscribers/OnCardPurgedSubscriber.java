package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCardPurgedSubscriber extends PCLCombatSubscriber
{
    void onPurge(AbstractCard card);
}
