package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cardmods.PermanentDamagePercentModifier;
import pinacolada.cardmods.TemporaryDamagePercentModifier;
import pinacolada.cards.base.PCLCard;
import pinacolada.utilities.GameUtilities;

public class ModifyDamagePercent extends ModifyCard {

    public ModifyDamagePercent(AbstractCard card, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        this(card, 1, costChange, permanent, relative, untilPlayed);
    }

    protected ModifyDamagePercent(AbstractCard card, int amount, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        super(card, amount, costChange, permanent, relative, untilPlayed);
    }

    @Override
    protected boolean canSelect(AbstractCard card) {
        return super.canSelect(card) && card.baseDamage >= 0;
    }

    public ModifyDamagePercent flash(Color flashColor) {
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

        modifyDamage(card, change, !permanent, untilPlayed);
    }

    public static void modifyDamage(AbstractCard card, int amount, boolean temporary, boolean untilPlayed) {
        if (temporary || untilPlayed) {
            TemporaryDamagePercentModifier.apply(card, amount, temporary, untilPlayed);
        }
        else {
            PermanentDamagePercentModifier.apply(card, amount);
        }

        if (card instanceof PCLCard) {
            ((PCLCard) card).updateDamageVars();
        }
        else {
            card.isDamageModified = false;
        }
    }
}
