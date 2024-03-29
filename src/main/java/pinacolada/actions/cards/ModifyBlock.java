package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cardmods.PermanentBlockModifier;
import pinacolada.cardmods.TemporaryBlockModifier;
import pinacolada.cards.base.PCLCard;
import pinacolada.utilities.GameUtilities;

public class ModifyBlock extends ModifyCard {

    public ModifyBlock(AbstractCard card, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        this(card, 1, costChange, permanent, relative, untilPlayed);
    }

    protected ModifyBlock(AbstractCard card, int amount, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        super(card, amount, costChange, permanent, relative, untilPlayed);
    }

    @Override
    protected boolean canSelect(AbstractCard card) {
        return super.canSelect(card) && card.baseBlock >= 0;
    }

    public ModifyBlock flash(Color flashColor) {
        this.flashColor = flashColor;

        return this;
    }

    @Override
    protected int getActualChange(AbstractCard card) {
        return relative ? change : change - card.block;
    }

    @Override
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        if (flashColor != null) {
            GameUtilities.flash(card, flashColor, true);
        }

        modifyBlock(card, getActualChange(card), !permanent, untilPlayed);
    }

    public static void modifyBlock(AbstractCard card, int amount, boolean temporary, boolean untilPlayed) {
        if (temporary || untilPlayed) {
            TemporaryBlockModifier.apply(card, amount, temporary, untilPlayed);
        }
        else {
            PermanentBlockModifier.apply(card, amount);
        }

        if (card instanceof PCLCard) {
            ((PCLCard) card).updateBlockVars();
        }
        else {
            card.isBlockModified = false;
        }
    }
}
