package pinacolada.actions.cardManipulation;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.utilities.GameUtilities;

public class ModifyTag extends GenericCardSelection
{
    protected PCLCardTag tag;
    protected int value;
    protected boolean relative;
    protected Color flashColor = EUIColors.gold(1).cpy();

    protected ModifyTag(AbstractCard card, CardGroup group, int amount, PCLCardTag tag, int value, boolean relative)
    {
        super(card, group, amount);

        this.tag = tag;
        this.value = value;
        this.relative = relative;
    }

    public ModifyTag(CardGroup group, int amount, PCLCardTag tag, int value, boolean relative)
    {
        this(null, group, amount, tag, value, relative);
    }

    public ModifyTag(CardGroup group, int amount, PCLCardTag tag, int value)
    {
        this(null, group, amount, tag, value, true);
    }

    public ModifyTag(AbstractCard card, PCLCardTag tag, int value, boolean relative)
    {
        this(card, null, 1, tag, value, relative);
    }

    public ModifyTag(AbstractCard card, PCLCardTag tag, int value)
    {
        this(card, null, 1, tag, value, true);
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
