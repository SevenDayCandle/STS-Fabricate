package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.CardSelection;

import java.util.ArrayList;

public class ScoutCards extends FetchFromPile
{
    public boolean reshuffleInstantly = false;

    public ScoutCards(String sourceName, int amount)
    {
        super(sourceName, amount, AbstractDungeon.player.drawPile);
        setOrigin(CardSelection.Top.toSelection());
        setOptions(true, false);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        SelectFromPile action = new ReshuffleFromPile(name, result.size(), player.hand).setDestination(CardSelection.Top.toSelection());
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
