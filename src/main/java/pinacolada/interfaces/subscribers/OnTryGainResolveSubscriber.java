package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public interface OnTryGainResolveSubscriber extends PCLCombatSubscriber
{
    int onTryGainResolve(AbstractCard card, AbstractPlayer p, int originalCost, boolean isActuallyGaining, boolean isFromMatch);
}