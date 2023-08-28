package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCreatureHealSubscriber extends PCLCombatSubscriber {
    int onHeal(AbstractCreature creature, int amount);
}
