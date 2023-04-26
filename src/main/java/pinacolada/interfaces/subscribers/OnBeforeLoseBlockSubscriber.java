package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnBeforeLoseBlockSubscriber extends PCLCombatSubscriber {
    void onBeforeLoseBlock(AbstractCreature creature, int amount, boolean noAnimation);
}
