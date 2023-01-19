package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnOrbApplyLockOnSubscriber extends PCLCombatSubscriber
{
    float onOrbApplyLockOn(AbstractCreature target, float dmg);
}
