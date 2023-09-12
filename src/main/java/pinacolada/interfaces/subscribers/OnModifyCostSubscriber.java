package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifyCostSubscriber extends PCLCombatSubscriber {
    int onModifyCost(int amount, AbstractCard card);
} 