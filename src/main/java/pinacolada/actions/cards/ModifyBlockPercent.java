package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cardmods.PermanentBlockModifier;
import pinacolada.cardmods.PermanentBlockPercentModifier;
import pinacolada.cardmods.TemporaryBlockModifier;
import pinacolada.cardmods.TemporaryBlockPercentModifier;
import pinacolada.cards.base.PCLCard;
import pinacolada.utilities.GameUtilities;

public class ModifyBlockPercent extends ModifyCard {

    public ModifyBlockPercent(AbstractCard card, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        this(card, 1, costChange, permanent, relative, untilPlayed);
    }

    protected ModifyBlockPercent(AbstractCard card, int amount, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        super(card, amount, costChange, permanent, relative, untilPlayed);
    }

    @Override
    protected boolean canSelect(AbstractCard card) {
        return super.canSelect(card) && card.baseBlock >= 0;
    }

    public ModifyBlockPercent flash(Color flashColor) {
        this.flashColor = flashColor;

        return this;
    }

    @Override
    protected int getActualChange(AbstractCard card) {
        return change;
    }

    @Override
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        if (flashColor != null) {
            GameUtilities.flash(card, flashColor, true);
        }

        modifyBlock(card, change, !permanent, untilPlayed);
    }

    public static void modifyBlock(AbstractCard card, int amount, boolean temporary, boolean untilPlayed) {
        if (temporary || untilPlayed) {
            TemporaryBlockPercentModifier.apply(card, amount, temporary, untilPlayed);
        }
        else {
            PermanentBlockPercentModifier.apply(card, amount);
        }

        if (card instanceof PCLCard) {
            ((PCLCard) card).updateBlockVars();
        }
        else {
            card.isBlockModified = false;
        }
    }
}
