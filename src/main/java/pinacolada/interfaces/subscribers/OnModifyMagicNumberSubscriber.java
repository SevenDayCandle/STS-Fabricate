package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnModifyMagicNumberSubscriber extends PCLCombatSubscriber
{
    float onModifyMagicNumber(float amount, AbstractCard c);
}
