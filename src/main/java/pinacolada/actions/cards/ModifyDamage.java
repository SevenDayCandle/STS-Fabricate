package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.utilities.GameUtilities;

public class ModifyDamage extends GenericCardSelection {
    protected boolean permanent;
    protected boolean relative;
    protected int change;
    protected Color flashColor = EUIColors.gold(1).cpy();

    public ModifyDamage(AbstractCard card, int change, boolean permanent, boolean relative) {
        this(card, 1, change, permanent, relative);
    }

    protected ModifyDamage(AbstractCard card, int amount, int change, boolean permanent, boolean relative) {
        super(card, amount);

        this.change = change;
        this.permanent = permanent;
        this.relative = relative;
    }

    @Override
    protected boolean canSelect(AbstractCard card) {
        return super.canSelect(card) && card.baseDamage >= 0;
    }

    public ModifyDamage flash(Color flashColor) {
        this.flashColor = flashColor;

        return this;
    }

    @Override
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        if (flashColor != null) {
            GameUtilities.flash(card, flashColor, true);
        }

        GameUtilities.modifyDamage(card, relative ? card.baseDamage + change : change, !permanent);
    }
}
