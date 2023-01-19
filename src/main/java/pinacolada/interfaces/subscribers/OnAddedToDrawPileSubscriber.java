package pinacolada.interfaces.subscribers;

import pinacolada.utilities.ListSelection;

public interface OnAddedToDrawPileSubscriber extends PCLCombatSubscriber
{
    void onAddedToDrawPile(boolean visualOnly, ListSelection.Mode destination);
}
