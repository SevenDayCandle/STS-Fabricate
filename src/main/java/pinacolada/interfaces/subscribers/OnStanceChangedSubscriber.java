package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.stances.AbstractStance;

public interface OnStanceChangedSubscriber extends PCLCombatSubscriber
{
    void onStanceChanged(AbstractStance oldStance, AbstractStance newStance);
}