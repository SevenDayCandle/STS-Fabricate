package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifyBlockFirstSubscriber extends PCLCombatSubscriber {
    float onModifyBlockFirst(float amount, AbstractCard card);
} 