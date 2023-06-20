package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.utilities.GameUtilities;

public abstract class ModifyCard extends GenericCardSelection {
    protected boolean permanent;
    protected boolean relative;
    protected boolean untilPlayed;
    protected int change;
    protected Color flashColor = EUIColors.gold(1).cpy();

    public ModifyCard(AbstractCard card, int change, boolean permanent, boolean relative) {
        this(card, 1, change, permanent, relative,false);
    }

    public ModifyCard(AbstractCard card, int change, boolean permanent, boolean relative, boolean untilPlayed) {
        this(card, 1, change, permanent, relative,untilPlayed);
    }

    protected ModifyCard(AbstractCard card, int amount, int change, boolean permanent, boolean relative, boolean untilPlayed) {
        super(card, amount);

        this.change = change;
        this.permanent = permanent;
        this.relative = relative;
        this.untilPlayed = untilPlayed;
    }

    public ModifyCard flash(Color flashColor) {
        this.flashColor = flashColor;

        return this;
    }

    protected abstract int getActualChange(AbstractCard card);

    @Override
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        if (flashColor != null) {
            GameUtilities.flash(card, flashColor, true);
        }
    }
}
