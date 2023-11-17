package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnTryUseXCostSubscriber extends PCLCombatSubscriber {
    default int onModifyXCost(int original, AbstractCard card) {
        return original;
    }
    default int onTryUseXCost(int original, AbstractCard card) {
        return original;
    }
}