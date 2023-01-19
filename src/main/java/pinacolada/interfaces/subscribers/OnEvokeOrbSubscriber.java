package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;

public interface OnEvokeOrbSubscriber extends PCLCombatSubscriber
{
    void onEvokeOrb(AbstractOrb orb);
}