package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnOrbApplyLockOnSubscriber extends PCLCombatSubscriber
{
    float onOrbApplyLockOn(AbstractCreature target, float dmg);
}
