package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class FetchFromPile extends SelectFromPile
{

    public FetchFromPile(String sourceName, int amount, CardGroup... groups)
    {
        super(ActionType.DRAW, sourceName, amount, groups);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        moveToPile(result, player.hand);
        super.complete(result);
    }

    @Override
    public String updateMessage()
    {
        return super.updateMessageInternal(PGR.core.strings.gridSelection.fetch);
    }
}
