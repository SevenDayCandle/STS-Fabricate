package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.resources.PCLPlayerData;
import pinacolada.utilities.GameUtilities;

public class LoadoutRelicSlot extends LoadoutSlot {

    public LoadoutRelicSlot(PCLLoadoutData container, String selected) {
        super(container, selected);
    }

    public LoadoutRelicSlot(LoadoutRelicSlot other) {
        super(other);
    }

    public static int getLoadoutValue(String item) {
        AbstractRelic r = RelicLibrary.getRelic(item);
        if (r instanceof PCLRelic) {
            return ((PCLRelic) r).relicData.getLoadoutValue();
        }
        return r != null ? PCLRelicData.getValueForRarity(r.tier) : 0;
    }

    @Override
    public int getEstimatedValue() {
        if (selected == null) {
            return 0;
        }
        return getLoadoutValue(selected);
    }

    @Override
    public boolean isBanned() {
        PCLPlayerData<?, ?, ?> playerData = container.loadout.getPlayerData();
        return playerData != null && playerData.config.bannedRelics.get().contains(selected);
    }

    @Override
    public boolean isLocked() {
        return GameUtilities.isRelicLocked(selected);
    }

    @Override
    protected void onSelect(String item) {
        UnlockTracker.markRelicAsSeen(selected);
    }
}
