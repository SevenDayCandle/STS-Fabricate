package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnAfterCardDrawnSubscriber
{
    void onAfterCardDrawn(AbstractCard card);
}
