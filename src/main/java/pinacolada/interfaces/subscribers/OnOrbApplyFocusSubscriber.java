package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;

public interface OnOrbApplyFocusSubscriber extends PCLCombatSubscriber
{
    void onApplyFocus(AbstractOrb orb);
}