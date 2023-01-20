package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.PCLAffinity;

@CombatSubscriber
public interface OnMatchBonusSubscriber extends PCLCombatSubscriber
{
    void onMatchBonus(AbstractCard card, PCLAffinity affinity);
}