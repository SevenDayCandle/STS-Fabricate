package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifyDamageGiveFirstSubscriber extends PCLCombatSubscriber {
    float onModifyDamageGiveFirst(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card);
} 