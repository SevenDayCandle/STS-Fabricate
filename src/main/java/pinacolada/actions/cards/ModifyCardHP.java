package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PCLEnum;
import pinacolada.utilities.GameUtilities;

public class ModifyCardHP extends GenericCardSelection {
    protected boolean permanent;
    protected boolean relative;
    protected int change;
    protected Color flashColor = EUIColors.gold(1).cpy();

    public ModifyCardHP(AbstractCard card, int change, boolean permanent, boolean relative) {
        this(card, 1, change, permanent, relative);
    }

    protected ModifyCardHP(AbstractCard card, int amount, int change, boolean permanent, boolean relative) {
        super(card, amount);

        this.change = change;
        this.permanent = permanent;
        this.relative = relative;
    }

    // Only summons use heal
    @Override
    protected boolean canSelect(AbstractCard card) {
        return super.canSelect(card) && card instanceof PCLCard && card.type == PCLEnum.CardType.SUMMON;
    }

    public ModifyCardHP flash(Color flashColor) {
        this.flashColor = flashColor;

        return this;
    }

    @Override
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        if (flashColor != null) {
            GameUtilities.flash(card, flashColor, true);
        }

        if (card instanceof PCLCard) {
            if (relative) {
                GameUtilities.modifySecondaryValueRelative((PCLCard) card, change, !permanent);
            }
            else {
                GameUtilities.modifySecondaryValue((PCLCard) card, change, !permanent);
            }
        }
    }
}
