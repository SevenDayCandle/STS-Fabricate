package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;
import pinacolada.utilities.ListSelection;

@CombatSubscriber
public interface OnAddedToDrawPileSubscriber extends PCLCombatSubscriber
{
    void onAddedToDrawPile(boolean visualOnly, ListSelection.Mode destination);
}
