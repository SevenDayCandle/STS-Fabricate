package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;

public interface OnTryChannelOrbSubscriber extends PCLCombatSubscriber
{
    AbstractOrb onTryChannelOrb(AbstractOrb orb);
}