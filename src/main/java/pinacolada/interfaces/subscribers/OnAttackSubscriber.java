package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnAttackSubscriber extends PCLCombatSubscriber
{
    void onAttack(DamageInfo info, int damageAmount, AbstractCreature target);
}
