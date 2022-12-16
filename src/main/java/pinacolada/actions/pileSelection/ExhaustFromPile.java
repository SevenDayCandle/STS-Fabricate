package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class ExhaustFromPile extends SelectFromPile
{

    public ExhaustFromPile(String sourceName, int amount, CardGroup... groups)
    {
        super(ActionType.EXHAUST, sourceName, amount, groups);
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
