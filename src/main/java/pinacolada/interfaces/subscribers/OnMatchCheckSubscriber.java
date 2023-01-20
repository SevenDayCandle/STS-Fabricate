package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnMatchCheckSubscriber extends PCLCombatSubscriber
{
    boolean onMatchCheck(AbstractCard var1);
}