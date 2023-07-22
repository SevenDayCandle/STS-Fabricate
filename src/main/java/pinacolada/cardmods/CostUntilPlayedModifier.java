package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.interfaces.subscribers.OnCardResetSubscriber;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class CostUntilPlayedModifier extends AbstractCardModifier implements OnCardResetSubscriber {
    protected AbstractCard card;
    protected int previousAmount;
    protected boolean temporary;
    public int change;

    public CostUntilPlayedModifier(int change, boolean temporary) {
        this.change = change;
        this.temporary = temporary;
    }

    public static CostUntilPlayedModifier apply(AbstractCard c, int change, boolean temporary) {
        CostUntilPlayedModifier mod = get(c);
        if (mod == null) {
            mod = new CostUntilPlayedModifier(change, temporary);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
            mod.temporary = mod.temporary & temporary;
        }
        return mod;
    }

    public static CostUntilPlayedModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof CostUntilPlayedModifier) {
                return (CostUntilPlayedModifier) mod;
            }
        }
        return null;
    }

    public void apply(AbstractCard card) {
        if (card.freeToPlay()) {
            previousAmount = 0;
            return;
        }

        final int currentCost = (card.costForTurn - previousAmount);
        previousAmount = change;

        final int newCost = currentCost + change;
        GameUtilities.modifyCostForCombat(card, Math.max(0, newCost), false);

        if (newCost < 0) {
            previousAmount -= newCost;
        }
    }

    public void unapply(AbstractCard card) {
        GameUtilities.modifyCostForCombat(card, Math.max(0, card.cost - change), false);
        card.resetAttributes();
    }

    @Override
    public void onCardReset(AbstractCard card) {
        if (card == this.card) {
            apply(card);
        }
    }

    public boolean removeOnCardPlayed(AbstractCard card) {
        return true;
    }

    public boolean removeAtEndOfTurn(AbstractCard card) {
        return temporary;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        subscribeToAll();
        this.card = card;
        apply(card);
    }

    @Override
    public void onRemove(AbstractCard card) {
        unsubscribeFromAll();
        this.card = null;
        unapply(card);
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new CostUntilPlayedModifier(change, temporary);
    }
}
