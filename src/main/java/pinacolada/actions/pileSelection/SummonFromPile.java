package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class SummonFromPile extends SelectFromPile
{
    protected PCLCardAlly target;
    public boolean allowSameSpot = false;

    public SummonFromPile(String sourceName, int amount, CardGroup... groups)
    {
        this(sourceName, null, amount, groups);
    }

    public SummonFromPile(String sourceName, PCLCardAlly target, int amount, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, amount, groups);

        this.target = target;
    }

    public SummonFromPile allowSameSpot(boolean value)
    {
        allowSameSpot = value;
        return this;
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        ArrayList<AbstractCard> summoned = new ArrayList<>();

        for (AbstractCard card : result)
        {
            if (!allowSameSpot && (target == null || target.hasCard()))
            {
                target = GameUtilities.getRandomSummon(false);
            }
            if (target == null)
            {
                complete(summoned);
                return;
            }
            if (card instanceof PCLCard)
            {
                PCLActions.bottom.summonAlly((PCLCard) card, target);
                summoned.add(card);
            }
        }

        super.complete(summoned);
    }

    @Override
    public String getActionMessage()
    {
        return PGR.core.tooltips.summon.title;
    }
}
