package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnAfterCardPlayedSubscriber extends PCLCombatSubscriber
{
    void onAfterCardPlayed(AbstractCard card);
}