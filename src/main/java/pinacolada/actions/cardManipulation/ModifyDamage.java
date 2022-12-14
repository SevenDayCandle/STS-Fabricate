package pinacolada.actions.cardManipulation;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.utilities.GameUtilities;

public class ModifyDamage extends GenericCardSelection
{
    protected boolean permanent;
    protected boolean relative;
    protected int change;
    protected Color flashColor = EUIColors.gold(1).cpy();

    protected ModifyDamage(AbstractCard card, CardGroup group, int amount, int change, boolean permanent, boolean relative)
    {
        super(card, group, amount);

        this.change = change;
        this.permanent = permanent;
        this.relative = relative;
    }

    public ModifyDamage(CardGroup group, int amount, int change, boolean permanent, boolean relative)
    {
        this(null, group, amount, change, permanent, relative);
    }

    public ModifyDamage(AbstractCard card, int change, boolean permanent, boolean relative)
    {
        this(card, null, 1, change, permanent, relative);
    }

    @Override
    protected boolean canSelect(AbstractCard card)
    {
        return super.canSelect(card) && card.baseDamage >= 0;
    }

    @Override
    protected void selectCard(AbstractCard card)
    {
        super.selectCard(card);

        if (flashColor != null)
        {
            GameUtilities.flash(card, flashColor, true);
        }

        GameUtilities.modifyDamage(card, relative ? card.baseDamage + change : change, !permanent);
    }

    public ModifyDamage flash(Color flashColor)
    {
        this.flashColor = flashColor;

        return this;
    }
}
