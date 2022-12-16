package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class RetainFromPile extends SelectFromPile
{

    public RetainFromPile(String sourceName, int amount, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, amount, groups);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        for (AbstractCard card : result)
        {
            GameUtilities.retain(card);
        }
        super.complete(result);
    }

    @Override
    public String getActionMessage()
    {
        return PGR.core.tooltips.retain.title;
    }
}
