package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.utilities.RotatingList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public abstract class LoadoutSlot<T, U extends LoadoutSlot.Item<T>> {
    public transient final PCLLoadoutData container;
    public transient final RotatingList<U> items;
    public U selected;

    public LoadoutSlot(PCLLoadoutData container) {

        this.items = new RotatingList<>();
        this.container = container;
    }

    public void addItem(T relic, int estimatedValue) {
        items.add(makeItem(relic, estimatedValue));
    }

    public void addItems(Collection<? extends T> items, int estimatedValue) {
        for (T data : items) {
            this.items.add(makeItem(data, estimatedValue));
        }
    }

    public boolean canRemove() {
        return (selected != null);
    }

    public LoadoutSlot<T, U> clear() {
        selected = null;
        return this;
    }

    public int findIndex(Predicate<U> predicate) {
        for (int i = 0; i < items.size(); i++) {
            if (predicate.test(items.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public int getEstimatedValue() {
        return (selected == null ? 0 : selected.estimatedValue);
    }

    public T getItem() {
        return selected != null ? selected.item : null;
    }

    public int getSlotIndex() {
        return getSlots().indexOf(this);
    }

    public abstract ArrayList<? extends LoadoutSlot<T, U>> getSlots();

    public abstract U makeItem(T item, int estimateValue);

    public abstract LoadoutSlot<T, U> makeCopy(PCLLoadoutData container);

    public void markAllSeen() {
        for (U item : items) {
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
            select(items.current());
        }
        else {
            select(items.next(true));
        }
    }

    public void previous() {
        if (selected == null) {
            select(items.current());
        }
        else {
            select(items.previous(true));
        }
    }

    public LoadoutSlot<T, U> select(T relic) {
        int i = 0;
        for (U item : items) {
            if (item.item == relic) {
                return select(i);
            }
            i += 1;
        }

        return null;
    }

    public LoadoutSlot<T, U> select(int index) {
        return select(items.setIndex(index));
    }

    public LoadoutSlot<T, U> select(U item) {
        selected = item;
        return this;
    }

    public LoadoutSlot<T, U> select(String id) {
        int i = 0;
        for (U item : items) {
            if (item.matches(id)) {
                return select(i);
            }
            i += 1;
        }

        return null;
    }

    public abstract static class Item<T> {
        public final LoadoutSlot<T, ?> slot;
        public final T item;
        public final int estimatedValue;

        public Item(LoadoutSlot<T, ?> slot, T item, int estimatedValue) {
            this.slot = slot;
            this.item = item;
            this.estimatedValue = estimatedValue;
        }

        public abstract boolean isBanned();

        public abstract boolean isLocked();

        public abstract boolean matches(String id);

        public abstract void markAsSeen();
    }
}
