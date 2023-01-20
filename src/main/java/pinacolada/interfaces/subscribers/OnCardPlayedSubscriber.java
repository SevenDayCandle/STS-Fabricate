package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardPlayedSubscriber extends PCLCombatSubscriber
{
    void onCardPlayed(AbstractCard card);
}