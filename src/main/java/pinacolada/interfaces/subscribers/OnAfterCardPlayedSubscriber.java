package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnAfterCardPlayedSubscriber
{
    void onAfterCardPlayed(AbstractCard card);
}