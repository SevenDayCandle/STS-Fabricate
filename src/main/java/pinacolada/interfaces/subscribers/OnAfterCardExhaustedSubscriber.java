package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnAfterCardExhaustedSubscriber extends PCLCombatSubscriber
{
    void onAfterCardExhausted(AbstractCard card);
}