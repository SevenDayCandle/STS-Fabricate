package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnTrySpendEnergySubscriber extends PCLCombatSubscriber {
    boolean canSpendEnergy(AbstractCard card, boolean originalValue);

    int onTrySpendEnergy(AbstractCard card, int originalCost);
}