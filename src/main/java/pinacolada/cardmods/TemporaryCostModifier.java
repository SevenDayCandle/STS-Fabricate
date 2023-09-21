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

    public static void tryRefresh(AbstractCard c, AbstractCreature owner, int base) {
        if (owner instanceof AbstractPlayer) {
            for (AbstractRelic relic : ((AbstractPlayer) owner).relics) {
                if (relic instanceof PCLRelic) {
                    base = ((PCLRelic) relic).atCostModify(base, c);
                }
            }
        }
        if (owner != null) {
            for (AbstractPower po : owner.powers) {
                if (po instanceof PCLPower) {
                    base = ((PCLPower) po).modifyCost(base, c);
                }
            }
        }
        base = CombatManager.onModifyCost(base, c);
        if (base != c.cost) {
            int baseDiff = base - c.cost;
            TemporaryCostModifier mod = get(c);
            if (mod == null) {
                mod = new TemporaryCostModifier(0, false, false);
                mod.baseDiff = baseDiff;
                CardModifierManager.addModifier(c, mod);
            }
            else if (mod.baseDiff != baseDiff) {
                mod.baseDiff = baseDiff;
                mod.apply(c);
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