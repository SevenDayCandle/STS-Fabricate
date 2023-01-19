package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnTryUseXCostSubscriber extends PCLCombatSubscriber
{
    int onTryUseXCost(int original, AbstractCard card);
}