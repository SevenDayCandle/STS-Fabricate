package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifyRightCountSubscriber extends PCLCombatSubscriber {
    int onModifyRightCount(int amount, AbstractCard card);
} 