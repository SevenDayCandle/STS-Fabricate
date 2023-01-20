package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.PCLUseInfo;

@CombatSubscriber
public interface OnNotMatchSubscriber extends PCLCombatSubscriber
{
    void onNotMatch(AbstractCard card, PCLUseInfo info);
}