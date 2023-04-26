package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardPurgedSubscriber extends PCLCombatSubscriber {
    void onPurge(AbstractCard card);
}
