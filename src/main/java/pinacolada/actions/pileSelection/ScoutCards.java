package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.actions.PCLActions;
import pinacolada.actions.basic.DrawCards;
import pinacolada.utilities.CardSelection;

import java.util.ArrayList;

public class ScoutCards extends DrawCards
{
    public boolean reshuffleInstantly = false;

    public ScoutCards(String sourceName, int amount)
    {
        super(sourceName, amount);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        SelectFromPile action = new ReshuffleFromPile(name, result.size(), player.hand).setDestination(CardSelection.Top);
        if (reshuffleInstantly)
        {
            PCLActions.top.add(action);
        }
        else
        {
            PCLActions.bottom.add(action);
        }

        super.complete(result);
    }

    public ScoutCards drawInstantly(boolean value)
    {
        reshuffleInstantly = value;

        return this;
    }
}
