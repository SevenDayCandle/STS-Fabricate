package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnOrbApplyLockOnSubscriber
{
    float onOrbApplyLockOn(AbstractCreature target, float dmg);
}
