package pinacolada.actions.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cardmods.CostUntilPlayedModifier;
import pinacolada.utilities.GameUtilities;

public class ModifyCost extends ModifyCard {

    public ModifyCost(AbstractCard card, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        this(card, 1, costChange, permanent, relative, untilPlayed);
    }

    protected ModifyCost(AbstractCard card, int amount, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        super(card, amount, costChange, permanent, relative, untilPlayed);
    }

    public static boolean canCardPass(AbstractCard card, int change) {
        return card.costForTurn >= 0 && (card.costForTurn != 0 || change > 0);
    }

    @Override
    protected boolean canSelect(AbstractCard card) {
        return super.canSelect(card) && canCardPass(card, getActualChange(card));
    }

    @Override
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        if (untilPlayed) {
            CostUntilPlayedModifier.apply(card, getActualChange(card), !permanent);

        }
        else {
            if (permanent) {
                GameUtilities.modifyCostForCombat(card, change, relative);
            }
            else {
                GameUtilities.modifyCostForTurn(card, change, relative);
            }
        }

    }

    protected int getActualChange(AbstractCard card) {
        return relative ? change : change - card.costForTurn;
    }
}
