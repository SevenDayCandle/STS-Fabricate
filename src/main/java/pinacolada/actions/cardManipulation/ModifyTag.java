package pinacolada.actions.cardManipulation;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.utilities.GameUtilities;

public class ModifyTag extends GenericCardSelection
{
    protected PCLCardTag tag;
    protected int value;
    protected boolean relative;
    protected Color flashColor = EUIColors.gold(1).cpy();

    protected ModifyTag(AbstractCard card, int amount, PCLCardTag tag, int value, boolean relative)
    {
        super(card, amount);

        this.tag = tag;
        this.value = value;
        this.relative = relative;
    }

    public ModifyTag(AbstractCard card, PCLCardTag tag, int value, boolean relative)
    {
        this(card, 1, tag, value, relative);
    }

    public ModifyTag(AbstractCard card, PCLCardTag tag, int value)
    {
        this(card, 1, tag, value, true);
    }

    @Override
    protected void selectCard(AbstractCard card)
    {
        super.selectCard(card);

        if (flashColor != null)
        {
            GameUtilities.flash(card, flashColor, true);
        }

        GameUtilities.modifyTag(card, tag, value, relative);
    }

    public ModifyTag flash(Color flashColor)
    {
        this.flashColor = flashColor;

        return this;
    }
}
