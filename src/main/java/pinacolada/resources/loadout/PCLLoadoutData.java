package pinacolada.resources.loadout;

import com.google.gson.reflect.TypeToken;
import extendedui.EUIUtils;
import extendedui.utilities.TupleT2;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

// Copied and modified from STS-AnimatorMod
public class PCLLoadoutData {
    private static final TypeToken<HashMap<PCLBaseStatEditor.StatType, Integer>> TValue = new TypeToken<HashMap<PCLBaseStatEditor.StatType, Integer>>() {
    };
    public static final TypeToken<TupleT2<String, Integer>> TTuple = new TypeToken<TupleT2<String, Integer>>() {
    };
    public static final TypeToken<LoadoutInfo> TInfo = new TypeToken<LoadoutInfo>() {
    };
    public final HashMap<PCLBaseStatEditor.StatType, Integer> values = new HashMap<>();
    public final ArrayList<LoadoutBlightSlot> blightSlots = new ArrayList<>();
    public final ArrayList<LoadoutCardSlot> cardSlots = new ArrayList<>();
    public final ArrayList<LoadoutRelicSlot> relicSlots = new ArrayList<>();
    public final PCLLoadout loadout;
    public int preset;

    public PCLLoadoutData(PCLLoadout loadout) {
        this.loadout = loadout;
        for (PCLBaseStatEditor.StatType type : PCLBaseStatEditor.StatType.values()) {
            values.put(type, 0);
        }
    }

    public PCLLoadoutData(PCLLoadout loadout, LoadoutInfo info) {
        this.loadout = loadout;
        preset = info.preset;
        values.putAll(EUIUtils.deserialize(info.values, TValue.getType()));
        for (String blight : info.blights) {
            if (blight != null) {
                addBlightSlot(blight);
            }
        }
        for (String blight : info.relics) {
            if (blight != null) {
                addRelicSlot(blight);
            }
        }
        for (LoadoutInfo.LoadoutCardInfo blight : info.cards) {
            if (blight != null && blight.id != null) {
                addCardSlot(blight.id, blight.count);
            }
        }
    }

    public PCLLoadoutData(PCLLoadoutData other) {
        loadout = other.loadout;
        preset = other.preset;
        values.putAll(other.values);
        for (LoadoutBlightSlot slot : other.blightSlots) {
            blightSlots.add(new LoadoutBlightSlot(slot));
        }
        for (LoadoutCardSlot slot : other.cardSlots) {
            cardSlots.add(new LoadoutCardSlot(slot));
        }
        for (LoadoutRelicSlot slot : other.relicSlots) {
            relicSlots.add(new LoadoutRelicSlot(slot));
        }
    }

    public LoadoutBlightSlot addBlightSlot(String selection) {
        final LoadoutBlightSlot slot = new LoadoutBlightSlot(this, selection);
        blightSlots.add(slot);

        return slot;
    }

    public LoadoutCardSlot addCardSlot(String selection) {
        final LoadoutCardSlot slot = new LoadoutCardSlot(this, selection);
        slot.onSelect(selection);
        cardSlots.add(slot);
        return slot;
    }

    public LoadoutCardSlot addCardSlot(String selection, int amount) {
        final LoadoutCardSlot slot = new LoadoutCardSlot(this, selection);
        slot.setAmount(amount);
        cardSlots.add(slot);

        return slot;
    }

    public LoadoutRelicSlot addRelicSlot(String selection) {
        final LoadoutRelicSlot slot = new LoadoutRelicSlot(this, selection);
        relicSlots.add(slot);

        return slot;
    }

    public void clear() {
        blightSlots.clear();
        cardSlots.clear();
        relicSlots.clear();
    }

    public LoadoutBlightSlot getBlightSlot(int index) {
        return blightSlots.get(index);
    }

    public LoadoutCardSlot getCardSlot(int index) {
        return cardSlots.get(index);
    }

    public LoadoutRelicSlot getRelicSlot(int index) {
        return relicSlots.get(index);
    }

    public PCLLoadoutData makeCopy() {
        return makeCopy(preset);
    }

    public PCLLoadoutData makeCopy(int preset) {
        return new PCLLoadoutData(this);
    }

    public PCLLoadoutValidation validate() {
        return PCLLoadoutValidation.createFrom(this);
    }

    public static class LoadoutInfo implements Serializable {
        static final long serialVersionUID = 1L;
        public String loadout;
        public int preset;
        public String values;
        public String[] blights;
        public String[] relics;
        public LoadoutCardInfo[] cards;

        public LoadoutInfo() {
        }

        public LoadoutInfo(String id, PCLLoadoutData data) {
            loadout = id;
            preset = data.preset;
            values = EUIUtils.serialize(data.values);
            blights = EUIUtils.arrayMapAsNonnull(data.blightSlots, String.class, d -> d.selected);
            cards = EUIUtils.arrayMapAsNonnull(data.cardSlots, LoadoutCardInfo.class, d -> d.selected != null ? new LoadoutCardInfo(d.selected, d.amount) : null);
            relics = EUIUtils.arrayMapAsNonnull(data.relicSlots, String.class, d -> d.selected);
        }

        public static class LoadoutCardInfo implements Serializable {
            static final long serialVersionUID = 1L;
            public String id;
            public Integer count;

            public LoadoutCardInfo(String id, Integer count) {
                this.id = id;
                this.count = count;
            }
        }
    }
}
