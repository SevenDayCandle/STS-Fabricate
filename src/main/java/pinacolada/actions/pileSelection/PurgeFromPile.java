package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;

public class PurgeFromPile extends SelectFromPile
{
    public PurgeFromPile(String sourceName, int amount, CardGroup... groups)
    {
        super(ActionType.EXHAUST, sourceName, null, amount, groups);
    }

    public PurgeFromPile(String sourceName, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        super(ActionType.EXHAUST, sourceName, null, amount, origin, groups);
    }

    public PurgeFromPile(String sourceName, AbstractCreature target, int amount, CardGroup... groups)
    {
        super(ActionType.EXHAUST, sourceName, target, amount, groups);
    }

    public PurgeFromPile(String sourceName, AbstractCreature target, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        super(ActionType.EXHAUST, sourceName, target, amount, origin, groups);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        moveToPile(result, CombatManager.PURGED_CARDS);
        super.complete(result);
    }

    public PurgeFromPile showEffect(boolean showEffect, boolean isRealtime)
    {
        this.showEffect = showEffect;
        this.realtime = isRealtime;

        return this;
    }

    @Override
    public String getActionMessage()
    {
        return PGR.core.tooltips.purge.title;
    }
}
