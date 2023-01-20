package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;
import pinacolada.misc.PCLUseInfo;

@CombatSubscriber
public interface OnMatchSubscriber extends PCLCombatSubscriber
{
    void onMatch(AbstractCard card, PCLUseInfo info);
}