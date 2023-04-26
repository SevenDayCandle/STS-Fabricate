package pinacolada.actions.piles;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.resources.PGR;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;

public class DiscardFromPile extends SelectFromPile {
    public DiscardFromPile(String sourceName, int amount, CardGroup... groups) {
        super(ActionType.DISCARD, sourceName, null, amount, groups);
    }

    public DiscardFromPile(String sourceName, int amount, ListSelection<AbstractCard> origin, CardGroup... groups) {
        super(ActionType.DISCARD, sourceName, null, amount, origin, groups);
    }

    public DiscardFromPile(String sourceName, AbstractCreature target, int amount, CardGroup... groups) {
        super(ActionType.DISCARD, sourceName, target, amount, groups);
    }

    public DiscardFromPile(String sourceName, AbstractCreature target, int amount, ListSelection<AbstractCard> origin, CardGroup... groups) {
        super(ActionType.DISCARD, sourceName, target, amount, origin, groups);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result) {
        moveToPile(result, player.discardPile);
        super.complete(result);
    }

    @Override
    public String getActionMessage() {
        return PGR.core.tooltips.discard.title;
    }
}
