package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardDrawnSubscriber extends PCLCombatSubscriber {
    void onCardDrawn(AbstractCard card);
}
