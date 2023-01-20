package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnTryUseXCostSubscriber extends PCLCombatSubscriber
{
    int onTryUseXCost(int original, AbstractCard card);
}