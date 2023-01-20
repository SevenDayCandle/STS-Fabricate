package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifyMagicNumberSubscriber extends PCLCombatSubscriber
{
    float onModifyMagicNumber(float amount, AbstractCard c);
}
