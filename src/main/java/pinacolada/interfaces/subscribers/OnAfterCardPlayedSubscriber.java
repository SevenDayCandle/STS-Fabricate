package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnAfterCardPlayedSubscriber extends PCLCombatSubscriber
{
    void onAfterCardPlayed(AbstractCard card);
}