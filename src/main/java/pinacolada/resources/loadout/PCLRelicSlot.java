package pinacolada.resources.loadout;

import extendedui.utilities.RotatingList;
import pinacolada.relics.PCLRelic;

import java.util.ArrayList;

public class PCLRelicSlot {
    public transient final PCLLoadoutData container;
    public transient final RotatingList<Item> relics;

    public Item selected;

    public PCLRelicSlot(PCLLoadoutData container) {

        this.relics = new RotatingList<>();
        this.container = container;
    }

    public void addItem(PCLRelic relic, int estimatedValue) {
        relics.add(new Item(relic, estimatedValue));
    }

    public void addItems(ArrayList<PCLRelic> items, int estimatedValue) {
        for (PCLRelic data : items) {
            relics.add(new Item(data, estimatedValue));
        }
    }

    public boolean canRemove() {
        return (selected != null);
    }

    public PCLRelicSlot clear() {
        selected = null;
        return this;
    }

    public int getEstimatedValue() {
        return (selected == null ? 0 : selected.estimatedValue);
    }

    public ArrayList<PCLRelicSlot.Item> getSelectableRelics() {
        final ArrayList<PCLRelicSlot.Item> relics = new ArrayList<>();
        for (Item item : this.relics) {
            boolean add = true;
            for (PCLRelicSlot slot : container.relicSlots) {
                if (slot != this && slot.getRelic() == item.relic) {
                    add = false;
                }
            }

            if (add) {
                relics.add(new PCLRelicSlot.Item(item.relic, item.estimatedValue));
            }
        }

        return relics;
    }

    public PCLRelic getRelic() {
        return selected != null ? selected.relic : null;
    }

    public int getSlotIndex() {
        return container.relicSlots.indexOf(this);
    }

    public PCLRelicSlot makeCopy(PCLLoadoutData container) {
        final PCLRelicSlot copy = new PCLRelicSlot(container);
        copy.relics.addAll(relics);
        if (selected != null) {
            copy.select(selected.relic);
        }

        return copy;
    }

    public PCLRelicSlot select(PCLRelic relic) {
        int i = 0;
        for (Item item : relics) {
            if (item.relic == relic) {
                return select(i);
            }
            i += 1;
        }

        return null;
    }

    public PCLRelicSlot select(int index) {
        return select(relics.setIndex(index));
    }

    public PCLRelicSlot select(Item item) {
        selected = item;
        return this;
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
            for (PCLRelicSlot s : container.relicSlots) {
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
                select((PCLRelic) null);
                return;
            }
        }
    }

    public PCLRelicSlot select(String id) {
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
        public final PCLRelic relic;
        public final int estimatedValue;

        public Item(PCLRelic relic, int estimatedValue) {
            this.relic = relic;
            this.estimatedValue = estimatedValue;
        }
    }
}
