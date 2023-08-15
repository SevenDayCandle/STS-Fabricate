package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.potions.AbstractPotion;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnTryObtainPotionSubscriber extends PCLCombatSubscriber {
    boolean tryObtainPotion(AbstractPotion potion);
}