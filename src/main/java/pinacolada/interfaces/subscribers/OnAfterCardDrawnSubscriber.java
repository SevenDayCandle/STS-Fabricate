package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnAfterCardDrawnSubscriber extends PCLCombatSubscriber
{
    void onAfterCardDrawn(AbstractCard card);
}
