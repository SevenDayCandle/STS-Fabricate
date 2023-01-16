package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.resources.PGR;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;

public class ReshuffleFromPile extends SelectFromPile
{
    public ReshuffleFromPile(String sourceName, int amount, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, null, amount, groups);
    }

    public ReshuffleFromPile(String sourceName, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, null, amount, origin, groups);
    }

    public ReshuffleFromPile(String sourceName, AbstractCreature target, int amount, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, target, amount, groups);
    }

    public ReshuffleFromPile(String sourceName, AbstractCreature target, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, target, amount, origin, groups);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        moveToPile(result, player.drawPile);
        super.complete(result);
    }

    @Override
    public String getActionMessage()
    {
        return PGR.core.tooltips.reshuffle.title;
    }
}
