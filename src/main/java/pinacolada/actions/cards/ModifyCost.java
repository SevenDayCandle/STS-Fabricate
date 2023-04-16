package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.utilities.GameUtilities;

public class ModifyCost extends GenericCardSelection
{
    protected boolean permanent;
    protected boolean relative;
    protected int costChange;
    protected Color flashColor = EUIColors.gold(1).cpy();

    protected ModifyCost(AbstractCard card, int amount, int costChange, boolean permanent, boolean relative)
    {
        super(card, amount);

        this.costChange = costChange;
        this.permanent = permanent;
        this.relative = relative;
    }

    public ModifyCost(AbstractCard card, int costChange, boolean permanent, boolean relative)
    {
        this(card, 1, costChange, permanent, relative);
    }

    @Override
    protected boolean canSelect(AbstractCard card)
    {
        return super.canSelect(card) && canCardPass(card, relative ? costChange : costChange - card.costForTurn);
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

    public static boolean canCardPass(AbstractCard card, int change)
    {
        return card.costForTurn >= 0 && (card.costForTurn != 0 || change > 0);
    }
}
