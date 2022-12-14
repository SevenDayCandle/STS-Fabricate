package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnTryUseXCostSubscriber
{
    int onTryUseXCost(int original, AbstractCard card);
}