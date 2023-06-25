package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCardUpgradeSubscriber extends PCLCombatSubscriber {
    void onUpgrade(AbstractCard card);
}
