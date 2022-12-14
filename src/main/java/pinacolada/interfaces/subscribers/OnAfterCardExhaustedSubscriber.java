package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnAfterCardExhaustedSubscriber
{
    void onAfterCardExhausted(AbstractCard card);
}