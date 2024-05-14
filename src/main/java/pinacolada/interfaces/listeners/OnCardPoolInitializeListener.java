package pinacolada.interfaces.listeners;

import com.megacrit.cardcrawl.cards.CardGroup;

import java.util.ArrayList;

public interface OnCardPoolInitializeListener {
    void onCardPoolInitialized(ArrayList<CardGroup> groups);
}
