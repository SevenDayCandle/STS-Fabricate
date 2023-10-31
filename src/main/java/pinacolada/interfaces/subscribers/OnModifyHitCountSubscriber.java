package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifyHitCountSubscriber extends PCLCombatSubscriber {
    int onModifyHitCount(int amount, AbstractCard card);
} 