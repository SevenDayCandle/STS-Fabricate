package pinacolada.actions.pileSelection;

import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class PurgeFromPile extends SelectFromPile
{
    protected Vector2 targetPosition;

    public PurgeFromPile(String sourceName, int amount, CardGroup... groups)
    {
        super(ActionType.SPECIAL, sourceName, amount, groups);
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
