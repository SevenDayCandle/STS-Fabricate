package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLAffinity;

public interface OnMatchBonusSubscriber
{
    void onMatchBonus(AbstractCard card, PCLAffinity affinity);
}