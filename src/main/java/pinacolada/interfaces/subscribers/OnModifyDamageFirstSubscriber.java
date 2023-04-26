package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifyDamageFirstSubscriber extends PCLCombatSubscriber {
    int onModifyDamageFirst(AbstractCreature target, DamageInfo info, int damage);
} 