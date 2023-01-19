package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLUseInfo;

public interface OnNotMatchSubscriber extends PCLCombatSubscriber
{
    void onNotMatch(AbstractCard card, PCLUseInfo info);
}