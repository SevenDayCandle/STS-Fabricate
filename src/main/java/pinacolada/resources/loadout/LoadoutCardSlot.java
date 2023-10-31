package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import pinacolada.cards.base.*;
import pinacolada.patches.library.CardLibraryPatches;
import pinacolada.resources.PCLPlayerData;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class LoadoutCardSlot extends LoadoutSlot {
    protected int amount;
    protected int currentMax;

    public LoadoutCardSlot(PCLLoadoutData container, String selected) {
        super(container, selected);
        updateCurrentMax();
    }

    public LoadoutCardSlot(LoadoutCardSlot other) {
        super(other);
        this.amount = other.amount;
        this.currentMax = other.currentMax;
    }

    public static int getLoadoutCardSort(AbstractCard a, AbstractCard b) {
        return getLoadoutCardSort(a.cardID, b.cardID);
    }

    public static int getLoadoutCardSort(String a, String b) {
        int aEst = LoadoutCardSlot.getLoadoutValue(a);
        if (aEst < 0) {
            aEst = -aEst * 1000;
        }
        int bEst = LoadoutCardSlot.getLoadoutValue(b);
        if (bEst < 0) {
            bEst = -bEst * 1000;
        }
        return aEst - bEst;
    }

    public static int getLoadoutValue(AbstractCard card) {
        if (card != null) {
            PCLCardData replacement = TemplateCardData.getTemplate(card.cardID);
            if (replacement != null) {
                return replacement.getLoadoutValue();
            }

            if (card instanceof PCLCard) {
                return ((PCLCard) card).cardData.getLoadoutValue();
            }
            return PCLCardData.getValueForRarity(card.rarity);
        }
        return 0;
    }

    public static int getLoadoutValue(String item) {
        PCLCardData replacement = PCLCardData.getStaticData(item);
        if (replacement != null) {
            return replacement.getLoadoutValue();
        }

        replacement = TemplateCardData.getTemplate(item);
        if (replacement != null) {
            return replacement.getLoadoutValue();
        }

        AbstractCard r = CardLibrary.getCard(item);
        if (r instanceof PCLCard) {
            return ((PCLCard) r).cardData.getLoadoutValue();
        }
        if (r != null) {
            return PCLCardData.getValueForRarity(r.rarity);
        }
        return 0;
    }

    public static int getMaxCardCopies(String item) {
        PCLCardData data = PCLCardData.getStaticData(item);
        return data != null ? data.maxCopies : -1;
    }

    public void add() {
        if (amount < currentMax) {
            amount += 1;
        }
    }

    public boolean canAdd() {
        return (selected != null) && amount < currentMax;
    }

    public boolean canDecrement() {
        return (selected != null) && amount > 1;
    }

    public boolean canRemove() {
        return (selected != null);
    }

    public void decrement() {
        if (amount > 1) {
            amount -= 1;
        }
    }

    public int getAmount() {
        return amount;
    }

    public int getCurrentMax() {
        return currentMax;
    }

    public int getEstimatedValue() {
        if (selected == null) {
            return 0;
        }
        return getLoadoutValue(selected) * amount;
    }

    @Override
    public boolean isBanned() {
        PCLPlayerData<?, ?, ?> playerData = container.loadout.getPlayerData();
        return playerData != null && playerData.config.bannedCards.get().contains(selected);
    }

    @Override
    public boolean isLocked() {
        return GameUtilities.isCardLocked(selected);
    }

    @Override
    protected void onSelect(String item) {
        GameUtilities.forceMarkCardAsSeen(selected);
    }

    public LoadoutCardSlot select(String item) {
        return select(item, item == null ? 0 : 1);
    }

    public LoadoutCardSlot select(String item, int amount) {
        selected = item;
        onSelect(item);
        updateCurrentMax();
        return this;
    }

    public LoadoutCardSlot select(PCLCardData data, int amount) {
        return select(data.ID, amount);
    }

    public LoadoutCardSlot setAmount(int amount) {
        this.amount = Math.min(amount, currentMax);
        return this;
    }

    protected void updateCurrentMax() {
        if (selected == null) {
            this.amount = 0;
            this.currentMax = 0;
        }
        else {
            int maxCopies = getMaxCardCopies(selected);
            if (maxCopies < 1) { // Infinite copies allowed
                currentMax = container.loadout.maxCardsPerSlot;
            }
            else {
                currentMax = Math.min(container.loadout.maxCardsPerSlot, maxCopies);
            }
            this.amount = Math.min(amount, currentMax);
        }
    }
}
