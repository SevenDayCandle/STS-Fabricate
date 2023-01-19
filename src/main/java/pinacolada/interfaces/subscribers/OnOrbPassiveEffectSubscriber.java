package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;

public interface OnOrbPassiveEffectSubscriber extends PCLCombatSubscriber
{
    void onOrbPassiveEffect(AbstractOrb orb);
}