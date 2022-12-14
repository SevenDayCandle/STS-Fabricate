package pinacolada.interfaces.subscribers;

import pinacolada.utilities.ListSelection;

public interface OnAddedToDrawPileSubscriber
{
    void onAddedToDrawPile(boolean visualOnly, ListSelection.Mode destination);
}
