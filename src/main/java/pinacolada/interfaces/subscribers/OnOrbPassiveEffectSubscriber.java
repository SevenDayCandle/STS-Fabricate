package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnOrbPassiveEffectSubscriber extends PCLCombatSubscriber
{
    void onOrbPassiveEffect(AbstractOrb orb);
}