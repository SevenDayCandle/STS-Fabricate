package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardCreatedSubscriber extends PCLCombatSubscriber
{
    void onCardCreated(AbstractCard card, boolean startOfBattle);
}
