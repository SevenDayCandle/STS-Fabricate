package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnAttackSubscriber
{
    void onAttack(DamageInfo info, int damageAmount, AbstractCreature target);
}
