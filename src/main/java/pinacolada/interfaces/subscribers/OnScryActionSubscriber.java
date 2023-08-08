package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnScryActionSubscriber extends PCLCombatSubscriber {
    void onScryAction(AbstractGameAction action);
}
