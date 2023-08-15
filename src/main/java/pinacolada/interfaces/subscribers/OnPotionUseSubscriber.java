package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.potions.AbstractPotion;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnPotionUseSubscriber extends PCLCombatSubscriber {
    void onUsePotion(AbstractPotion potion);
}