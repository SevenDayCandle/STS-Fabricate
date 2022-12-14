package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.stances.AbstractStance;

public interface OnStanceChangedSubscriber
{
    void onStanceChanged(AbstractStance oldStance, AbstractStance newStance);
}