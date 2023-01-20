package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnAfterCardExhaustedSubscriber extends PCLCombatSubscriber
{
    void onAfterCardExhausted(AbstractCard card);
}