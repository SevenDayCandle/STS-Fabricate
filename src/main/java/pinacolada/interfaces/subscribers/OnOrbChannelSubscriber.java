package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnOrbChannelSubscriber extends PCLCombatSubscriber {
    void onChannelOrb(AbstractOrb orb);
}