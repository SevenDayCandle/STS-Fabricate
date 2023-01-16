package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.cards.base.CardSelection;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class ScryCards extends DiscardFromPile
{
    public ScryCards(String sourceName, int amount)
    {
        super(sourceName, amount, AbstractDungeon.player.drawPile);
        setMaxChoices(amount, CardSelection.Top.toSelection());

        initialize(amount);
    }

    @Override
    public String updateMessage()
    {
        return super.updateMessageInternal(PGR.core.strings.gridSelection.scry);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        for (AbstractPower p : player.powers)
        {
            p.onScry();
        }
        for (AbstractCard c : result)
        {
            c.triggerOnScry();
        }

        super.complete(result);
    }
}

