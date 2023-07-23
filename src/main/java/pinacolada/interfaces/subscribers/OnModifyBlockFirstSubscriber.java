package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifyBlockFirstSubscriber extends PCLCombatSubscriber {
    float onModifyBlockFirst(float amount, AbstractCard card);
} 