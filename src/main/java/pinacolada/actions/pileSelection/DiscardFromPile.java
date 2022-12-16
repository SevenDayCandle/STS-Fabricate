package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class DiscardFromPile extends SelectFromPile
{
    public DiscardFromPile(String sourceName, int amount, CardGroup... groups)
    {
        super(ActionType.DISCARD, sourceName, amount, groups);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        moveToPile(result, player.discardPile);
        super.complete(result);
    }

    @Override
    public String getActionMessage()
    {
        return PGR.core.tooltips.discard.title;
    }
}
