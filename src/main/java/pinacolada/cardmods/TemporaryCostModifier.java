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

// Copied and modified from STS-AnimatorMod
// Used by temporary cost modification effects on a specific card
@AbstractCardModifier.SaveIgnore
public class TemporaryCostModifier extends AbstractCardModifier implements OnCardResetSubscriber {
    protected transient AbstractCard card;
    protected transient int previousChange;
    protected transient boolean temporary;
    protected transient boolean untilPlayed;
    protected transient int change;
    protected transient int baseDiff;

    public TemporaryCostModifier(int change, boolean temporary, boolean untilPlayed) {
        this.change = change;
        this.temporary = temporary;
        this.untilPlayed = untilPlayed;
    }

    public static TemporaryCostModifier apply(AbstractCard c, int change, boolean temporary, boolean untilPlayed) {
        TemporaryCostModifier mod = get(c);
        if (mod == null) {
            mod = new TemporaryCostModifier(change, temporary, untilPlayed);
            CardModifierManager.addModifier(c, mod);
        }
        else {
            mod.change += change;
            mod.temporary = mod.temporary & temporary;
            mod.untilPlayed = mod.untilPlayed & untilPlayed;
        }
        return mod;
    }

    public static TemporaryCostModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof TemporaryCostModifier) {
                return (TemporaryCostModifier) mod;
            }
        }
        return null;
    }

    /* Update the cost modifier to the calculated cost from powers. Called in applyPowersToBlock in regular cards and refresh in PCLCard.
     * Base is the baseline card cost to compare with, and var is any additional precalculations that should be added on to the result */
    public static void tryRefresh(AbstractCard c, AbstractCreature owner, int base, int var) {
        if (c.cost > -1) {
            TemporaryCostModifier mod = get(c);
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
                    mod = new TemporaryCostModifier(0, false, false);
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
        TemporaryCostModifier c = new TemporaryCostModifier(change, temporary, untilPlayed);
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

    public boolean removeAtEndOfTurn(AbstractCard card) {
        return temporary;
    }

    public boolean removeOnCardPlayed(AbstractCard card) {
        return untilPlayed;
    }

    public void unapply(AbstractCard card) {
        GameUtilities.modifyCostForTurn(card, Math.max(0, card.costForTurn - getActualChange()), false);
        card.resetAttributes();
    }
}
