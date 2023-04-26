package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardResetSubscriber extends PCLCombatSubscriber {
    void onCardReset(AbstractCard card);
}