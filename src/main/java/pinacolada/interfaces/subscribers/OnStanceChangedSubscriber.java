package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.stances.AbstractStance;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnStanceChangedSubscriber extends PCLCombatSubscriber {
    void onStanceChanged(AbstractStance oldStance, AbstractStance newStance);
}