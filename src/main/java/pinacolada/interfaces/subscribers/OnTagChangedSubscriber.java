package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;
import pinacolada.cards.base.tags.PCLCardTag;

@CombatSubscriber
public interface OnTagChangedSubscriber extends PCLCombatSubscriber
{
    void onTagChanged(AbstractCard card, PCLCardTag tag, int value);
}