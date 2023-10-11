package pinacolada.resources.loadout;

import com.google.gson.reflect.TypeToken;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.utilities.TupleT2;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;

import java.util.ArrayList;
import java.util.HashMap;

// Copied and modified from STS-AnimatorMod
public class PCLLoadoutData {
    private static final TypeToken<HashMap<PCLBaseStatEditor.StatType, Integer>> TValue = new TypeToken<HashMap<PCLBaseStatEditor.StatType, Integer>>() {
    };
    public static final TypeToken<TupleT2<String, Integer>> TTuple = new TypeToken<TupleT2<String, Integer>>() {
    };
    public final HashMap<PCLBaseStatEditor.StatType, Integer> values = new HashMap<>();
    public final ArrayList<LoadoutBlightSlot> blightSlots = new ArrayList<>();
    public final ArrayList<LoadoutCardSlot> cardSlots = new ArrayList<>();
    public final ArrayList<LoadoutRelicSlot> relicSlots = new ArrayList<>();
    public final PCLLoadout loadout;
    public final String ID;
    public String name;

    public PCLLoadoutData(PCLLoadout loadout) {
        this.loadout = loadout;
        this.ID = PCLLoadoutDataInfo.makeNewID(loadout);
        this.name = EUIRM.strings.generic2(loadout.getName(), loadout.presets.size() + 1);
        for (PCLBaseStatEditor.StatType type : PCLBaseStatEditor.StatType.values()) {
            values.put(type, 0);
        }
    }

    public PCLLoadoutData(PCLLoadout loadout, PCLLoadoutDataInfo info) {
        this.loadout = loadout;
        ID = info.ID;
        name = info.name;
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
        for (PCLLoadoutDataInfo.LoadoutCardInfo blight : info.cards) {
            if (blight != null && blight.id != null) {
                addCardSlot(blight.id, blight.count);
            }
        }
    }

    public PCLLoadoutData(PCLLoadoutData other) {
        loadout = other.loadout;
        ID = other.ID;
        name = other.name;
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
        return new PCLLoadoutData(this);
    }

    public PCLLoadoutValidation validate() {
        return PCLLoadoutValidation.createFrom(this);
    }

}
