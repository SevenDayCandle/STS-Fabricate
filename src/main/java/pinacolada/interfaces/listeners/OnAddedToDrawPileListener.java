package pinacolada.interfaces.listeners;

import pinacolada.utilities.ListSelection;

public interface OnAddedToDrawPileListener
{
    void onAddedToDrawPile(boolean visualOnly, ListSelection.Mode destination);
}
