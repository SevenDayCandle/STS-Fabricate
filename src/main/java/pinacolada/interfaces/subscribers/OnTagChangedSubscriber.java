package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.fields.PCLCardTag;

public interface OnTagChangedSubscriber extends PCLCombatSubscriber
{
    void onTagChanged(AbstractCard card, PCLCardTag tag, int value);
}