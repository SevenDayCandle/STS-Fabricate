package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLUseInfo;

public interface OnMatchSubscriber extends PCLCombatSubscriber
{
    void onMatch(AbstractCard card, PCLUseInfo info);
}