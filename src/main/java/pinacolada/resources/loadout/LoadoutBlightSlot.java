package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class LoadoutBlightSlot extends LoadoutSlot<AbstractBlight, LoadoutBlightSlot.Item> {

    public LoadoutBlightSlot(PCLLoadoutData container) {
        super(container);
    }

    @Override
    public ArrayList<LoadoutBlightSlot> getSlots() {
        return container.blightSlots;
    }

    public LoadoutBlightSlot makeCopy(PCLLoadoutData container) {
        final LoadoutBlightSlot copy = new LoadoutBlightSlot(container);
        copy.items.addAll(items);
        if (selected != null) {
            copy.select(selected.item);
        }

        return copy;
    }

    @Override
    public Item makeItem(AbstractBlight item, int estimateValue) {
        return new Item(this, item, estimateValue);
    }

    public static class Item extends LoadoutSlot.Item<AbstractBlight> {

        public Item(LoadoutBlightSlot slot, AbstractBlight relic, int estimatedValue) {
            super(slot, relic, estimatedValue);
        }

        @Override
        public boolean matches(String id) {
            return item.blightID.equals(id);
        }

        public boolean isBanned() {
            AbstractPlayerData<?, ?> playerData = slot.container.loadout.getPlayerData();
            return playerData != null && playerData.config.bannedRelics.get().contains(item.blightID);
        }

        public boolean isLocked() {
            return false;
        }

        public void markAsSeen() {
        }
    }
}
