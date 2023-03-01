package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.resources.PCLEnum;
import pinacolada.utilities.GameUtilities;

public class ModifyPriority extends GenericCardSelection
{
    protected boolean permanent;
    protected boolean relative;
    protected int change;
    protected Color flashColor = EUIColors.gold(1).cpy();

    protected ModifyPriority(AbstractCard card, int amount, int change, boolean permanent, boolean relative)
    {
        super(card, amount);

        this.change = change;
        this.permanent = permanent;
        this.relative = relative;
    }

    public ModifyPriority(AbstractCard card, int change, boolean permanent, boolean relative)
    {
        this(card, 1, change, permanent, relative);
    }

    @Override
    protected boolean canSelect(AbstractCard card)
    {
        return super.canSelect(card) && card instanceof EditorCard && card.type == PCLEnum.CardType.SUMMON;
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

    public ModifyPriority flash(Color flashColor)
    {
        this.flashColor = flashColor;

        return this;
    }
}
