package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.utilities.GameActions;

import java.util.ArrayList;

public class CycleCards extends DiscardFromPile
{
    public boolean drawInstantly = false;

    public CycleCards(String sourceName, int amount, boolean isRandom)
    {
        super(sourceName, amount, AbstractDungeon.player.hand);
        setOptions(isRandom, true);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        if (drawInstantly)
        {
            GameActions.top.draw(result.size());
        }
        else
        {
            GameActions.bottom.draw(result.size());
        }

        super.complete(result);
    }

    @Override
    public String updateMessage()
    {
        return super.updateMessageInternal(GamblingChipAction.TEXT[1]);
    }

    public CycleCards drawInstantly(boolean value)
    {
        drawInstantly = value;

        return this;
    }
}
