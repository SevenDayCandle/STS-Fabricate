package pinacolada.actions.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cardmods.PermanentCostModifier;
import pinacolada.cardmods.PermanentDamagePercentModifier;
import pinacolada.cardmods.TemporaryCostModifier;
import pinacolada.cardmods.TemporaryDamagePercentModifier;
import pinacolada.cards.base.PCLCard;
import pinacolada.utilities.GameUtilities;

public class ModifyCost extends ModifyCard {

    public ModifyCost(AbstractCard card, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        this(card, 1, costChange, permanent, relative, untilPlayed);
    }

    protected ModifyCost(AbstractCard card, int amount, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        super(card, amount, costChange, permanent, relative, untilPlayed);
    }

    // 0-cost cards shouldn't get targeted by cost lowering effects
    public static boolean canCardPass(AbstractCard card, int change) {
        return card.costForTurn >= 0 && (card.costForTurn != 0 || change > 0);
    }

    @Override
    protected boolean canSelect(AbstractCard card) {
        return super.canSelect(card) && canCardPass(card, change);
    }

    @Override
    protected int getActualChange(AbstractCard card) {
        return relative ? change : change - card.costForTurn;
    }

    @Override
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        modifyCost(card, getActualChange(card), !permanent, untilPlayed);
    }

    public static void modifyCost(AbstractCard card, int amount, boolean temporary, boolean untilPlayed) {
        if (temporary || untilPlayed) {
            TemporaryCostModifier.apply(card, amount, temporary, untilPlayed);
        }
        else {
            PermanentCostModifier.apply(card, amount);
        }
    }
}
