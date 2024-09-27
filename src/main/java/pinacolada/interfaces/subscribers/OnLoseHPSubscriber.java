package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnLoseHPSubscriber extends PCLCombatSubscriber {
    int onLoseHP(AbstractCreature p, DamageInfo info, int amount);
}
