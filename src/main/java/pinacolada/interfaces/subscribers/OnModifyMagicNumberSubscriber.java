package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnModifyMagicNumberSubscriber
{
    float onModifyMagicNumber(float amount, AbstractCard c);
}
