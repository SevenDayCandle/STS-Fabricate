package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.resources.PGR;

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
            PCLActions.top.draw(result.size());
        }
        else
        {
            PCLActions.bottom.draw(result.size());
        }

        super.complete(result);
    }

    @Override
    public String getActionMessage()
    {
        return PGR.core.tooltips.cycle.title;
    }

    public CycleCards drawInstantly(boolean value)
    {
        drawInstantly = value;

        return this;
    }
}
