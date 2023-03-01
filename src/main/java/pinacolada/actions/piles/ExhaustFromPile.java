package pinacolada.actions.piles;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.resources.PGR;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;

public class ExhaustFromPile extends SelectFromPile
{
    public ExhaustFromPile(String sourceName, int amount, CardGroup... groups)
    {
        super(ActionType.EXHAUST, sourceName, null, amount, groups);
    }

    public ExhaustFromPile(String sourceName, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        super(ActionType.EXHAUST, sourceName, null, amount, origin, groups);
    }

    public ExhaustFromPile(String sourceName, AbstractCreature target, int amount, CardGroup... groups)
    {
        super(ActionType.EXHAUST, sourceName, target, amount, groups);
    }

    public ExhaustFromPile(String sourceName, AbstractCreature target, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        super(ActionType.EXHAUST, sourceName, target, amount, origin, groups);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        moveToPile(result, player.exhaustPile);
        super.complete(result);
    }

    @Override
    public String getActionMessage()
    {
        return PGR.core.tooltips.exhaust.title;
    }
}
