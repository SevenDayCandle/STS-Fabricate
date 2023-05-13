package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnReceiveDamageFirstSubscriber extends PCLCombatSubscriber {
    int onReceiveDamageFirst(AbstractCreature target, DamageInfo info, int damage);
} 