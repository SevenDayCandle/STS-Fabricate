package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public interface OnTrySpendEnergySubscriber
{
    int onTrySpendEnergy(AbstractCard card, AbstractPlayer p, int originalCost);
}