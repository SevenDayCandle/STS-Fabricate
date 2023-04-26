package pinacolada.actions.piles;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;

public class RetainCards extends SelectFromPile {

    public RetainCards(String sourceName, int amount, CardGroup... groups) {
        super(ActionType.CARD_MANIPULATION, sourceName, null, amount, groups);
    }

    public RetainCards(String sourceName, int amount, ListSelection<AbstractCard> origin, CardGroup... groups) {
        super(ActionType.CARD_MANIPULATION, sourceName, null, amount, origin, groups);
    }

    public RetainCards(String sourceName, AbstractCreature target, int amount, CardGroup... groups) {
        super(ActionType.CARD_MANIPULATION, sourceName, target, amount, groups);
    }

    public RetainCards(String sourceName, AbstractCreature target, int amount, ListSelection<AbstractCard> origin, CardGroup... groups) {
        super(ActionType.CARD_MANIPULATION, sourceName, target, amount, origin, groups);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result) {
        for (AbstractCard card : result) {
            GameUtilities.retain(card);
        }
        super.complete(result);
    }

    @Override
    public String getActionMessage() {
        return PGR.core.tooltips.retain.title;
    }
}
