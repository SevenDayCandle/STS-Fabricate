package pinacolada.actions.cardManipulation;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.utilities.GameUtilities;

public class ModifyCost extends GenericCardSelection
{
    protected boolean permanent;
    protected boolean relative;
    protected int costChange;
    protected Color flashColor = EUIColors.gold(1).cpy();

    protected ModifyCost(AbstractCard card, CardGroup group, int amount, int costChange, boolean permanent, boolean relative)
    {
        super(card, group, amount);

        this.costChange = costChange;
        this.permanent = permanent;
        this.relative = relative;
    }

    public ModifyCost(CardGroup group, int amount, int costChange, boolean permanent, boolean relative)
    {
        this(null, group, amount, costChange, permanent, relative);
    }

    public ModifyCost(AbstractCard card, int costChange, boolean permanent, boolean relative)
    {
        this(card, null, 1, costChange, permanent, relative);
    }

    @Override
    protected boolean canSelect(AbstractCard card)
    {
        return super.canSelect(card) && card.costForTurn >= 0;
    }

    @Override
    protected void selectCard(AbstractCard card)
    {
        super.selectCard(card);

        if (flashColor != null)
        {
            GameUtilities.flash(card, flashColor, true);
        }

        if (permanent)
        {
            GameUtilities.modifyCostForCombat(card, costChange, relative);
        }
        else
        {
            GameUtilities.modifyCostForTurn(card, costChange, relative);
        }
    }

    public ModifyCost flash(Color flashColor)
    {
        this.flashColor = flashColor;

        return this;
    }
}
