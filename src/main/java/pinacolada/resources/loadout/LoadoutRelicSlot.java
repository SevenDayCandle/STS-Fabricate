package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.utilities.RotatingList;
import pinacolada.relics.PCLRelic;

import java.util.ArrayList;

public class LoadoutRelicSlot {
    public transient final PCLLoadoutData container;
    public transient final RotatingList<Item> relics;

    public Item selected;

    public LoadoutRelicSlot(PCLLoadoutData container) {

        this.relics = new RotatingList<>();
        this.container = container;
    }

    public void addItem(AbstractRelic relic, int estimatedValue) {
        relics.add(new Item(relic, estimatedValue));
    }

    public void addItems(ArrayList<AbstractRelic> items, int estimatedValue) {
        for (AbstractRelic data : items) {
            relics.add(new Item(data, estimatedValue));
        }
    }

    public boolean canRemove() {
        return (selected != null);
    }

    public LoadoutRelicSlot clear() {
        selected = null;
        return this;
    }

    public int getEstimatedValue() {
        return (selected == null ? 0 : selected.estimatedValue);
    }

    public AbstractRelic getRelic() {
        return selected != null ? selected.relic : null;
    }

    public ArrayList<LoadoutRelicSlot.Item> getSelectableRelics() {
        final ArrayList<LoadoutRelicSlot.Item> relics = new ArrayList<>();
        for (Item item : this.relics) {
            boolean add = true;
            for (LoadoutRelicSlot slot : container.relicSlots) {
                if (slot != this && slot.getRelic() == item.relic) {
                    add = false;
                }
            }

            if (add) {
                relics.add(new LoadoutRelicSlot.Item(item.relic, item.estimatedValue));
            }
        }

        return relics;
    }

    public int getSlotIndex() {
        return container.relicSlots.indexOf(this);
    }

    public LoadoutRelicSlot makeCopy(PCLLoadoutData container) {
        final LoadoutRelicSlot copy = new LoadoutRelicSlot(container);
        copy.relics.addAll(relics);
        if (selected != null) {
            copy.select(selected.relic);
        }

        return copy;
    }

    public void next() {
        if (selected == null) {
            select(relics.current());
        }
        else {
            select(relics.next(true));
        }

        int i = 0;
        while (true) {
            int currentIndex = i;
            for (LoadoutRelicSlot s : container.relicSlots) {
                if (s != this && selected.relic == s.getRelic()) {
                    select(relics.next(true));
                    i += 1;
                    break;
                }
            }

            if (currentIndex == i) {
                return;
            }
            else if (i >= relics.size()) {
                select((AbstractRelic) null);
                return;
            }
        }
    }

    public LoadoutRelicSlot select(AbstractRelic relic) {
        int i = 0;
        for (Item item : relics) {
            if (item.relic == relic) {
                return select(i);
            }
            i += 1;
        }

        return null;
    }

    public LoadoutRelicSlot select(int index) {
        return select(relics.setIndex(index));
    }

    public LoadoutRelicSlot select(Item item) {
        selected = item;
        return this;
    }

    public LoadoutRelicSlot select(String id) {
        int i = 0;
        for (Item item : relics) {
            if (item.relic.relicId.equals(id)) {
                return select(i);
            }
            i += 1;
        }

        return null;
    }

    public static class Item {
        public final AbstractRelic relic;
        public final int estimatedValue;

        public Item(AbstractRelic relic, int estimatedValue) {
            this.relic = relic;
            this.estimatedValue = estimatedValue;
        }
    }
}