package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
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
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        if (flashColor != null) {
            GameUtilities.flash(card, flashColor, true);
        }

        GameUtilities.modifyDamage(card, relative && permanent && !untilPlayed ? getActualChange(card) : change, !permanent, untilPlayed);
    }

    @Override
    protected int getActualChange(AbstractCard card) {
        return relative ? card.baseDamage + change : change;
    }
}
