package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.resources.PGR;
import pinacolada.utilities.CardSelection;

import java.util.ArrayList;

public class ScoutCards extends FetchFromPile
{
    public boolean reshuffleInstantly = false;

    public ScoutCards(String sourceName, int amount)
    {
        super(sourceName, amount, AbstractDungeon.player.drawPile);
        setOrigin(CardSelection.Top);
        setOptions(true, false);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        SelectFromPile action = new ReshuffleFromPile(name, result.size(), groups).setDestination(CardSelection.Top);
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

    @Override
    public String getActionMessage()
    {
        return PGR.core.tooltips.scout.title;
    }

    public ScoutCards drawInstantly(boolean value)
    {
        reshuffleInstantly = value;

        return this;
    }
}
