package pinacolada.interfaces.listeners;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnAddToDeckListener {
    default boolean onAddToDeck(AbstractCard card) {
        return onAddToDeck();
    }

    default boolean onAddToDeck() {
        return true;
    }
}