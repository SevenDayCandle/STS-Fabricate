package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnModifyDamageLastSubscriber
{
    int onModifyDamageLast(AbstractCreature target, DamageInfo info, int damage);
} 