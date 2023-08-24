package pinacolada.resources.loadout;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.utilities.RotatingList;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.function.Predicate;

// Copied and modified from STS-AnimatorMod
public class LoadoutCardSlot extends LoadoutSlot<String, LoadoutCardSlot.Item> {
    public static final int MAX_LIMIT = 6;
    public int amount;
    public int currentMax;
    public int max;
    public int min;

    public LoadoutCardSlot(PCLLoadoutData container) {
        this(container, 0, MAX_LIMIT);
    }

    public LoadoutCardSlot(PCLLoadoutData container, int min, int max) {
        super(container);
        if (min > max) {
            throw new RuntimeException("Min can't be greater than max.");
        }

        this.min = min;
        this.max = max;
    }

    public void add() {
        if (amount < max && amount < currentMax) {
            amount += 1;
        }
    }

    public void addItem(PCLCardData data, int estimatedValue) {
        addItem(data.ID, estimatedValue);
    }

    public boolean canAdd() {
        return (selected != null) && amount < max && amount < currentMax;
    }

    public boolean canDecrement() {
        return (selected != null) && amount > 1 && amount > min;
    }

    public boolean canRemove() {
        return (selected != null) && min <= 0;
    }

    public LoadoutCardSlot clear() {
        super.clear();
        this.amount = 0;
        return this;
    }

    public void decrement() {
        if (amount > 1) {
            amount -= 1;
        }
    }

    public AbstractCard getCard(boolean refresh) {
        return selected != null ? selected.getCard(refresh) : null;
    }

    public int getEstimatedValue() {
        return amount * (selected == null ? 0 : selected.estimatedValue);
    }

    public String getSelectedID() {
        return selected != null ? selected.item : null;
    }

    @Override
    public ArrayList<? extends LoadoutSlot<String, Item>> getSlots() {
        return container.cardSlots;
    }

    @Override
    public Item makeItem(String item, int estimateValue) {
        return new Item(this, item, estimateValue);
    }

    public boolean isIDBanned(String id) {
        AbstractPlayerData<?, ?> playerData = container.loadout.getPlayerData();
        return playerData != null && playerData.config.bannedCards.get().contains(id);
    }

    public boolean isInvalid() {
        PCLCustomCardSlot slot = PCLCustomCardSlot.get(selected.item);
        if (slot != null) {
            return !container.loadout.allowCustoms();
        }
        return selected.isLocked() || selected.isBanned();
    }

    public LoadoutCardSlot makeCopy(PCLLoadoutData container) {
        final LoadoutCardSlot copy = new LoadoutCardSlot(container, min, max);
        copy.items.addAll(items);
        if (selected != null) {
            copy.select(selected.item, amount);
        }

        return copy;
    }

    public LoadoutCardSlot select(String item) {
        return select(item, item == null ? 0 : 1);
    }

    public LoadoutCardSlot select(Item item, int amount) {
        selected = item;
        if (item == null) {
            this.amount = 0;
        }
        else {
            int maxCopies = selected.maxCopies();
            currentMax = Math.min(max, maxCopies >= min ? maxCopies : max);
            if (currentMax <= 0) {
                currentMax = MAX_LIMIT;
            }
            this.amount = MathUtils.clamp(amount, min, currentMax);
        }

        return this;
    }

    public LoadoutCardSlot select(PCLCardData data, int amount) {
        return select(data.ID, amount);
    }

    public LoadoutCardSlot select(int index, int amount) {
        return select(items.setIndex(index), amount);
    }

    public LoadoutCardSlot select(String id, int amount) {
        int i = 0;
        for (Item item : items) {
            if (item.item.equals(id)) {
                return select(i, amount);
            }
            i += 1;
        }

        return null;
    }

    public static class Item extends LoadoutSlot.Item<String> {

        protected AbstractCard card;

        public Item(LoadoutCardSlot slot, String id, int estimatedValue) {
            super(slot, id, estimatedValue);
        }

        public AbstractCard getCard(boolean forceRefresh) {
            if (card == null || forceRefresh) {
                AbstractCard eCard = CardLibrary.getCard(item);
                if (eCard != null) {
                    card = eCard.makeStatEquivalentCopy();
                }
            }

            return card;
        }

        public PCLLoadout getLoadout() {
            AbstractCard c = getCard(false);
            if (c instanceof PCLCard) {
                return ((PCLCard) c).cardData.loadout;
            }
            return null;
        }

        @Override
        public boolean matches(String id) {
            return item.equals(id);
        }

        public boolean isBanned() {
            AbstractPlayerData<?, ?> playerData = slot.container.loadout.getPlayerData();
            return playerData != null && playerData.config.bannedCards.get().contains(item);
        }

        public boolean isLocked() {
            return GameUtilities.isCardLocked(item);
        }

        public void markAsSeen() {
            if (!UnlockTracker.isCardSeen(item)) {
                UnlockTracker.markCardAsSeen(item);
            }
        }

        public int maxCopies() {
            AbstractCard c = getCard(false);
            if (c instanceof PCLCard) {
                return ((PCLCard) c).cardData.maxCopies;
            }
            return MAX_LIMIT;
        }
    }
}
