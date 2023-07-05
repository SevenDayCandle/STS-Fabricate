package pinacolada.resources.loadout;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.utilities.RotatingList;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.function.Predicate;

// Copied and modified from STS-AnimatorMod
public class LoadoutCardSlot {
    public static final int MAX_LIMIT = 6;
    public transient final PCLLoadoutData container;
    public transient final RotatingList<Item> cards;

    public Item selected;
    public int amount;
    public int currentMax;
    public int max;
    public int min;

    public LoadoutCardSlot(PCLLoadoutData container) {
        this(container, 0, MAX_LIMIT);
    }

    public LoadoutCardSlot(PCLLoadoutData container, int min, int max) {
        if (min > max) {
            throw new RuntimeException("Min can't be greater than max.");
        }

        this.cards = new RotatingList<>();
        this.container = container;
        this.min = min;
        this.max = max;
    }

    public void add() {
        if (amount < max && amount < currentMax) {
            amount += 1;
        }
    }

    public void addItem(PCLCardData data, int estimatedValue) {
        cards.add(new Item(data, estimatedValue));
    }

    public void addItem(String data, int estimatedValue) {
        cards.add(new Item(data, estimatedValue));
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
        return select(null);
    }

    public void decrement() {
        if (amount > 1) {
            amount -= 1;
        }
    }

    public int findIndex(Predicate<Item> predicate) {
        for (int i = 0; i < cards.size(); i++) {
            if (predicate.test(cards.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public AbstractCard getCard(boolean refresh) {
        return selected != null ? selected.getCard(refresh) : null;
    }

    public String getSelectedID() {
        return selected != null ? selected.ID : null;
    }

    public int getEstimatedValue() {
        return amount * (selected == null ? 0 : selected.estimatedValue);
    }

    public ArrayList<LoadoutCardSlot.Item> getSelectableCards() {
        final ArrayList<LoadoutCardSlot.Item> cards = new ArrayList<>();
        for (Item item : this.cards) {
            // Custom cards should not be treated as locked in this effect
            boolean add = !isIDBanned(item.ID) && (!item.isLocked() || PCLCustomCardSlot.get(item.ID) != null);
            if (add) {
                for (LoadoutCardSlot slot : container.cardSlots) {
                    if (slot != this && item.ID.equals(slot.getSelectedID())) {
                        add = false;
                    }
                }
            }


            if (add) {
                cards.add(item);
            }
        }

        cards.sort((a, b) -> {
            if (a.estimatedValue == b.estimatedValue) {
                return StringUtils.compare(a.getCard(false).name, b.getCard(false).name);
            }
            return a.estimatedValue - b.estimatedValue;
        });

        return cards;
    }

    public int getSlotIndex() {
        return container.cardSlots.indexOf(this);
    }

    public boolean isIDBanned(String id) {
        PCLAbstractPlayerData<?, ?> playerData = container.loadout.getPlayerData();
        return playerData != null && playerData.config.bannedCards.get().contains(id);
    }

    public boolean isInvalid() {
        PCLCustomCardSlot slot = PCLCustomCardSlot.get(selected.ID);
        if (slot != null) {
            return !container.loadout.allowCustoms();
        }
        return selected.isLocked() || isIDBanned(selected.ID);
    }

    public LoadoutCardSlot makeCopy(PCLLoadoutData container) {
        final LoadoutCardSlot copy = new LoadoutCardSlot(container, min, max);
        copy.cards.addAll(cards);
        if (selected != null) {
            copy.select(selected.ID, amount);
        }

        return copy;
    }

    public void markAllSeen() {
        for (Item item : cards) {
            item.markAsSeen();
        }
    }

    public void markCurrentSeen() {
        if (selected != null) {
            selected.markAsSeen();
        }
    }

    public void next() {
        if (selected == null) {
            select(cards.current());
        }
        else {
            select(cards.next(true));
        }

        int i = 0;
        while (true) {
            int currentIndex = i;
            for (LoadoutCardSlot s : container.cardSlots) {
                if (s != this && selected.ID.equals(s.getSelectedID())) {
                    select(cards.next(true));
                    i += 1;
                    break;
                }
            }

            if (currentIndex == i) {
                return;
            }
            else if (i >= cards.size()) {
                select(null);
                return;
            }
        }
    }

    public LoadoutCardSlot select(Item item) {
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
        return select(cards.setIndex(index), amount);
    }

    public LoadoutCardSlot select(String id, int amount) {
        int i = 0;
        for (Item item : cards) {
            if (item.ID.equals(id)) {
                return select(i, amount);
            }
            i += 1;
        }

        return null;
    }

    public static class Item {
        public final String ID;
        public final int estimatedValue;

        protected AbstractCard card;

        public Item(PCLCardData data, int estimatedValue) {
            this(data.ID, estimatedValue);
        }

        public Item(String id, int estimatedValue) {
            this.ID = id;
            this.estimatedValue = estimatedValue;
        }

        public AbstractCard getCard(boolean forceRefresh) {
            if (card == null || forceRefresh) {
                AbstractCard eCard = CardLibrary.getCard(ID);
                if (eCard != null) {
                    card = eCard.makeStatEquivalentCopy();
                }
            }

            return card;
        }

        public boolean isLocked() {
            return GameUtilities.isCardLocked(ID);
        }

        public void markAsSeen() {
            if (!UnlockTracker.isCardSeen(ID)) {
                UnlockTracker.markCardAsSeen(ID);
            }
        }

        public int maxCopies() {
            AbstractCard c = getCard(false);
            if (c instanceof PCLCard) {
                return ((PCLCard) c).cardData.maxCopies;
            }
            return MAX_LIMIT;
        }

        public PCLLoadout getLoadout() {
            AbstractCard c = getCard(false);
            if (c instanceof PCLCard) {
                return ((PCLCard) c).cardData.loadout;
            }
            return null;
        }
    }
}
