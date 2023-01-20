package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardScrySubscriber extends PCLCombatSubscriber
{
    void onScry(AbstractCard card);
}
