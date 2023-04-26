package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardRetainSubscriber extends PCLCombatSubscriber {
    void onRetain(AbstractCard card);
}
