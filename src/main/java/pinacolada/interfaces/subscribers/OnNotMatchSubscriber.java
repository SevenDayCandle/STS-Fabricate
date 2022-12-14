package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLUseInfo;

public interface OnNotMatchSubscriber
{
    void onNotMatch(AbstractCard card, PCLUseInfo info);
}