package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnDamageOverrideSubscriber extends PCLCombatSubscriber {
    float onDamageOverride(AbstractCreature target, DamageInfo.DamageType type, float damage, AbstractCard card);
} 
