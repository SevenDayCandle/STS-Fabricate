package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnAfterCardDrawnSubscriber extends PCLCombatSubscriber
{
    void onAfterCardDrawn(AbstractCard card);
}
