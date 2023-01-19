package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;

public interface OnChannelOrbSubscriber extends PCLCombatSubscriber
{
    void onChannelOrb(AbstractOrb orb);
}