package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnCardResetSubscriber;
import pinacolada.powers.PCLPower;
import pinacolada.relics.PCLRelic;
import pinacolada.utilities.GameUtilities;

// Used by temporary cost modification effects on a specific card
public class PermanentCostModifier extends AbstractCardModifier implements OnCardResetSubscriber {
    protected transient AbstractCard card;
    protected transient int previousChange;
    protected int change;
    protected int baseDiff;

    public PermanentCostModifier(int change) {
        this.change = change;
    }

    public static PermanentCostModifier apply(AbstractCard c, int change) {
        PermanentCostModifier mod = get(c);
        if (mod == null) {
            mod = new PermanentCostModifier(change);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
        }
        return mod;
    }

    public static PermanentCostModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof PermanentCostModifier) {
                return (PermanentCostModifier) mod;
            }
        }
        return null;
    }

    /* Update the cost modifier to the calculated cost from powers. Called in applyPowersToBlock in regular cards and refresh in PCLCard.
     * Base is the baseline card cost to compare with, and var is any additional precalculations that should be added on to the result */
    public static void tryRefresh(AbstractCard c, AbstractCreature owner, int base, int var) {
        if (c.cost > -1) {
            PermanentCostModifier mod = get(c);
            int baseDiff = 0;
            if (mod != null) {
                baseDiff = mod.baseDiff;
                base -= mod.baseDiff;
            }
            int res = base + var;
            if (owner instanceof AbstractPlayer) {
                for (AbstractRelic relic : ((AbstractPlayer) owner).relics) {
                    if (relic instanceof PCLRelic) {
                        res = ((PCLRelic) relic).atCostModify(res, c);
                    }
                }
            }
            if (owner != null) {
                for (AbstractPower po : owner.powers) {
                    if (po instanceof PCLPower) {
                        res = ((PCLPower) po).modifyCost(res, c);
                    }
                }
            }
            res = CombatManager.onModifyCost(res, c);
            int diff = res - base;
            if (diff != baseDiff) {
                if (mod == null) {
                    mod = new PermanentCostModifier(0);
                    mod.baseDiff = diff;
                    CardModifierManager.addModifier(c, mod);
                }
                else {
                    mod.baseDiff = diff;
                    mod.apply(c);
                }
            }
        }
    }

    public void apply(AbstractCard card) {
        if (card.freeToPlay()) {
            previousChange = 0;
            return;
        }

        final int currentCost = (card.costForTurn - previousChange);
        previousChange = getActualChange();

        final int newCost = currentCost + previousChange;
        GameUtilities.modifyCostForTurn(card, Math.max(0, newCost), false);

        if (newCost < 0) {
            previousChange -= newCost;
        }
    }

    public int getActualChange() {
        return change + baseDiff;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        PermanentCostModifier c = new PermanentCostModifier(change);
        c.baseDiff = this.baseDiff;
        return c;
    }

    @Override
    public void onCardReset(AbstractCard card) {
        if (card == this.card) {
            // Invalidate previous change because costForTurn gets reset
            previousChange = 0;
            apply(card);
        }
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

    public void unapply(AbstractCard card) {
        GameUtilities.modifyCostForTurn(card, Math.max(0, card.costForTurn - getActualChange()), false);
        card.resetAttributes();
    }
}
