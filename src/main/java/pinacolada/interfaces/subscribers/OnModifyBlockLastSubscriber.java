package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifyBlockLastSubscriber extends PCLCombatSubscriber {
    float onModifyBlockLast(float amount, AbstractCard card);
} 