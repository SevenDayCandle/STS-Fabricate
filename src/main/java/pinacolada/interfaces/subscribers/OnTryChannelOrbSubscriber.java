package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnTryChannelOrbSubscriber extends PCLCombatSubscriber {
    AbstractOrb onTryChannelOrb(AbstractOrb orb);
}