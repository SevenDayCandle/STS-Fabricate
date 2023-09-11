package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class LoadoutBlightSlot extends LoadoutSlot {

    public LoadoutBlightSlot(PCLLoadoutData container, String selected) {
        super(container, selected);
    }

    public LoadoutBlightSlot(LoadoutBlightSlot other) {
        super(other);
    }

    @Override
    public int getEstimatedValue() {
        return 0;
    }

    @Override
    public boolean isBanned() {
        return false;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    protected void onSelect(String item) {

    }


}
