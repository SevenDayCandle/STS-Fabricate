package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnEvokeOrbSubscriber extends PCLCombatSubscriber
{
    void onEvokeOrb(AbstractOrb orb);
}