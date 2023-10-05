package pinacolada.resources.loadout;

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
