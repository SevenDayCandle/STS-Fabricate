package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnOrbEvokeSubscriber extends PCLCombatSubscriber {
    void onEvokeOrb(AbstractOrb orb);
}