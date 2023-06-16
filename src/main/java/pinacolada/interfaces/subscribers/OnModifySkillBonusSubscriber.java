package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifySkillBonusSubscriber extends PCLCombatSubscriber {
    float onModifySkillBonus(float amount, AbstractCard c);
}
