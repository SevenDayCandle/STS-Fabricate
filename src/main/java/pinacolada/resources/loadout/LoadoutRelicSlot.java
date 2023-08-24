package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class LoadoutRelicSlot extends LoadoutSlot<AbstractRelic, LoadoutRelicSlot.Item> {

    public LoadoutRelicSlot(PCLLoadoutData container) {
        super(container);
    }

    @Override
    public ArrayList<LoadoutRelicSlot> getSlots() {
        return container.relicSlots;
    }

    public LoadoutRelicSlot makeCopy(PCLLoadoutData container) {
        final LoadoutRelicSlot copy = new LoadoutRelicSlot(container);
        copy.items.addAll(items);
        if (selected != null) {
            copy.select(selected.item);
        }

        return copy;
    }

    @Override
    public Item makeItem(AbstractRelic item, int estimateValue) {
        return new Item(this, item, estimateValue);
    }

    public static class Item extends LoadoutSlot.Item<AbstractRelic> {

        public Item(LoadoutRelicSlot slot, AbstractRelic relic, int estimatedValue) {
            super(slot, relic, estimatedValue);
        }

        @Override
        public boolean matches(String id) {
            return item.relicId.equals(id);
        }

        public boolean isBanned() {
            AbstractPlayerData<?, ?> playerData = slot.container.loadout.getPlayerData();
            return playerData != null && playerData.config.bannedRelics.get().contains(item.relicId);
        }

        public boolean isLocked() {
            return GameUtilities.isRelicLocked(item.relicId);
        }

        public void markAsSeen() {
            if (!UnlockTracker.isRelicSeen(item.relicId)) {
                UnlockTracker.markRelicAsSeen(item.relicId);
            }
        }
    }
}
