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
    public final ArrayList<LoadoutCardSlot> cardSlots = new ArrayList<>();
    public final ArrayList<LoadoutRelicSlot> relicSlots = new ArrayList<>();
    public final PCLLoadout loadout;
    public int preset;

    public PCLLoadoutData(PCLLoadout loadout) {
        this.loadout = loadout;
        loadout.initializeData(this);
    }

    public PCLLoadoutData(PCLLoadout loadout, LoadoutInfo info) {
        this.loadout = loadout;
        loadout.initializeData(this);
        info.fill(this);
    }

    public PCLLoadoutData(PCLLoadoutData other) {
        loadout = other.loadout;
        preset = other.preset;
        values.putAll(other.values);
        for (LoadoutCardSlot slot : other.cardSlots) {
            cardSlots.add(slot.makeCopy(other));
        }
        for (LoadoutRelicSlot slot : other.relicSlots) {
            relicSlots.add(slot.makeCopy(other));
        }
    }

    public LoadoutCardSlot addCardSlot() {
        return addCardSlot(0, LoadoutCardSlot.MAX_LIMIT);
    }

    public LoadoutCardSlot addCardSlot(int min, int max) {
        final LoadoutCardSlot slot = new LoadoutCardSlot(this, min, max);
        cardSlots.add(slot);

        return slot;
    }

    public LoadoutRelicSlot addRelicSlot() {
        final LoadoutRelicSlot slot = new LoadoutRelicSlot(this);
        relicSlots.add(slot);

        return slot;
    }

    public int cardsSize() {
        return cardSlots.size();
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

    public int relicsSize() {
        return relicSlots.size();
    }

    public PCLLoadoutValidation validate() {
        return PCLLoadoutValidation.createFrom(this);
    }

    public static class LoadoutInfo implements Serializable {
        public String loadout;
        public int preset;
        public String values;
        public String[] relics;
        public LoadoutCardInfo[] cards;

        public LoadoutInfo() {

        }

        public LoadoutInfo(String id, PCLLoadoutData data) {
            loadout = id;
            preset = data.preset;
            values = EUIUtils.serialize(data.values);
            cards = EUIUtils.arrayMap(data.cardSlots, LoadoutCardInfo.class, d -> d.selected != null ? new LoadoutCardInfo(d.selected.ID, d.amount) : null);
            relics = EUIUtils.arrayMap(data.relicSlots, String.class, d -> d.selected != null ? d.selected.relic.relicId : null);
        }

        public void fill(PCLLoadoutData data) {
            data.preset = preset;
            data.values.putAll(EUIUtils.deserialize(values, TValue.getType()));
            for (int i = 0; i < relics.length; i++) {
                data.getRelicSlot(i).select(relics[i]);
            }
            for (int i = 0; i < cards.length; i++) {
                if (cards[i] != null) {
                    data.getCardSlot(i).select(cards[i].id, cards[i].count);
                }
                else {
                    data.getCardSlot(i).select(null);
                }
            }
        }

        public static class LoadoutCardInfo implements Serializable {
            public String id;
            public Integer count;

            public LoadoutCardInfo(String id, Integer count) {
                this.id = id;
                this.count = count;
            }
        }
    }
}
