package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.utilities.RotatingList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public abstract class LoadoutSlot {
    protected final PCLLoadoutData container;
    public String selected;

    public LoadoutSlot(PCLLoadoutData container, String selected) {
        this.container = container;
        this.selected = selected;
    }

    public LoadoutSlot(LoadoutSlot other) {
        this.container = other.container;
        this.selected = other.selected;
    }

    public boolean canRemove() {
        return (selected != null);
    }

    public LoadoutSlot select(String item) {
        selected = item;
        onSelect(item);
        return this;
    }

    public abstract int getEstimatedValue();

    public abstract boolean isBanned();

    public abstract boolean isLocked();

    protected abstract void onSelect(String item);
}
