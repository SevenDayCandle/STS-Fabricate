package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnMatchCheckSubscriber extends PCLCombatSubscriber
{
    boolean onMatchCheck(AbstractCard var1, AbstractCard var2);
}