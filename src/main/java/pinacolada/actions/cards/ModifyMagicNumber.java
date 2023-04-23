package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.cards.base.PCLCard;
import pinacolada.utilities.GameUtilities;

public class ModifyMagicNumber extends GenericCardSelection
{
    protected boolean permanent;
    protected boolean relative;
    protected int change;
    protected Color flashColor = EUIColors.gold(1).cpy();

    protected ModifyMagicNumber(AbstractCard card, int amount, int change, boolean permanent, boolean relative)
    {
        super(card, amount);

        this.change = change;
        this.permanent = permanent;
        this.relative = relative;
    }

    public ModifyMagicNumber(AbstractCard card, int change, boolean permanent, boolean relative)
    {
        this(card, 1, change, permanent, relative);
    }

    // Only affect PCL cards because other cards can glitch or crash with this effect
    @Override
    protected boolean canSelect(AbstractCard card)
    {
        return super.canSelect(card) && card instanceof PCLCard;
    }

    @Override
    protected void selectCard(AbstractCard card)
    {
        super.selectCard(card);

        if (flashColor != null)
        {
            GameUtilities.flash(card, flashColor, true);
        }

        GameUtilities.modifyMagicNumber(card, relative ? card.baseMagicNumber + change : change, !permanent);
    }

    public ModifyMagicNumber flash(Color flashColor)
    {
        this.flashColor = flashColor;

        return this;
    }
}
