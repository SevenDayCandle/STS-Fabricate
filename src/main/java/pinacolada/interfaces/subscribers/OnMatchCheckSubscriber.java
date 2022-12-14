package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnMatchCheckSubscriber
{
    boolean onMatchCheck(AbstractCard var1, AbstractCard var2);
}