package pinacolada.actions.piles;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardSelection;

import java.util.ArrayList;

public class ScoutCards extends FetchFromPile
{
    public boolean reshuffleInstantly = false;

    public ScoutCards(String sourceName, int amount)
    {
        super(sourceName, amount, PCLCardSelection.Top.toSelection(), AbstractDungeon.player.drawPile);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        if (result.size() > 0)
        {
            SelectFromPile action = new ReshuffleFromPile(name, result.size(), player.hand).setDestination(PCLCardSelection.Top.toSelection());
            if (reshuffleInstantly)
            {
                PCLActions.top.add(action);
            }
            else
            {
                PCLActions.bottom.add(action);
            }
        }

        super.complete(result);
    }

    public ScoutCards drawInstantly(boolean value)
    {
        reshuffleInstantly = value;

        return this;
    }
}
