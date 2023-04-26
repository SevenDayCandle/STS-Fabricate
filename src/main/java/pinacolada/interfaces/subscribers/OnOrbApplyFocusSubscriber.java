package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnOrbApplyFocusSubscriber extends PCLCombatSubscriber {
    void onApplyFocus(AbstractOrb orb);
}