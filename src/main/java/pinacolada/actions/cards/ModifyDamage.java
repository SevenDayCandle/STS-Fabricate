package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cardmods.PermanentDamageModifier;
import pinacolada.cardmods.TemporaryDamageModifier;
import pinacolada.cards.base.PCLCard;
import pinacolada.utilities.GameUtilities;

public class ModifyDamage extends ModifyCard {

    public ModifyDamage(AbstractCard card, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        this(card, 1, costChange, permanent, relative, untilPlayed);
    }

    protected ModifyDamage(AbstractCard card, int amount, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        super(card, amount, costChange, permanent, relative, untilPlayed);
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
    protected int getActualChange(AbstractCard card) {
        return relative ? change : change - card.damage;
    }

    @Override
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        if (flashColor != null) {
            GameUtilities.flash(card, flashColor, true);
        }

        modifyDamage(card, permanent && !untilPlayed ? getActualChange(card) : change, !permanent, untilPlayed);
    }

    public static void modifyDamage(AbstractCard card, int amount, boolean temporary, boolean untilPlayed) {
        if (temporary || untilPlayed) {
            TemporaryDamageModifier.apply(card, amount, temporary, untilPlayed);
        }
        else {
            PermanentDamageModifier.apply(card, amount);
        }

        if (card instanceof PCLCard) {
            ((PCLCard) card).updateDamageVars();
        }
        else {
            card.isDamageModified = false;
        }
    }
}
