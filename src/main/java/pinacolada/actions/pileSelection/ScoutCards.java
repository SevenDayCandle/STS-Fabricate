package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.utilities.CardSelection;
import pinacolada.utilities.GameActions;

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
            GameActions.top.add(action);
        }
        else
        {
            GameActions.bottom.add(action);
        }

        super.complete(result);
    }

    @Override
    public String updateMessage()
    {
        return super.updateMessageInternal(GamblingChipAction.TEXT[1]);
    }

    public ScoutCards drawInstantly(boolean value)
    {
        reshuffleInstantly = value;

        return this;
    }
}
