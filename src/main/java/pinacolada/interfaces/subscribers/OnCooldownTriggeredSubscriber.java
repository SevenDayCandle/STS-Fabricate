package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;
import pinacolada.interfaces.providers.CooldownProvider;

@CombatSubscriber
public interface OnCooldownTriggeredSubscriber extends PCLCombatSubscriber {
    boolean onCooldownTriggered(CooldownProvider cooldown, AbstractCreature s, AbstractCreature m);
}
