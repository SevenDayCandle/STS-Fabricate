package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.utilities.GameUtilities;

public class ModifyBlock extends GenericCardSelection
{
    protected boolean permanent;
    protected boolean relative;
    protected int change;
    protected Color flashColor = EUIColors.gold(1).cpy();

    protected ModifyBlock(AbstractCard card, int amount, int change, boolean permanent, boolean relative)
    {
        super(card, amount);

        this.change = change;
        this.permanent = permanent;
        this.relative = relative;
    }

    public ModifyBlock(AbstractCard card, int change, boolean permanent, boolean relative)
    {
        this(card, 1, change, permanent, relative);
    }

    public ModifyBlock flash(Color flashColor)
    {
        this.flashColor = flashColor;

        return this;
    }

    @Override
    protected void selectCard(AbstractCard card)
    {
        super.selectCard(card);

        if (flashColor != null)
        {
            GameUtilities.flash(card, flashColor, true);
        }

        GameUtilities.modifyBlock(card, relative ? card.baseBlock + change : change, !permanent);
    }
}
