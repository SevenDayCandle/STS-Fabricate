package pinacolada.resources.loadout;

import com.google.gson.reflect.TypeToken;
import extendedui.EUIUtils;
import extendedui.utilities.TupleT2;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

// Copied and modified from STS-AnimatorMod
public class PCLLoadoutData
{
    private static final TypeToken<HashMap<PCLBaseStatEditor.StatType, Integer>> TValue = new TypeToken<HashMap<PCLBaseStatEditor.StatType, Integer>>() {};
    public static final TypeToken<TupleT2<String, Integer>> TTuple = new TypeToken<TupleT2<String, Integer>>(){};
    public static final TypeToken<LoadoutInfo> TInfo = new TypeToken<LoadoutInfo>(){};
    public final HashMap<PCLBaseStatEditor.StatType, Integer> values = new HashMap<>();
    public final ArrayList<PCLCardSlot> cardSlots = new ArrayList<>();
    public final ArrayList<PCLRelicSlot> relicSlots = new ArrayList<>();
    public int preset;

    protected PCLLoadoutData()
    {
    }

    public PCLLoadoutData(PCLLoadout loadout)
    {
        loadout.initializeData(this);
    }

    public PCLLoadoutData(PCLLoadout loadout, LoadoutInfo info)
    {
        loadout.initializeData(this);
        info.fill(this);
    }

    public PCLCardSlot addCardSlot()
    {
        return addCardSlot(0, PCLCardSlot.MAX_LIMIT);
    }

    public PCLCardSlot addCardSlot(int min, int max)
    {
        final PCLCardSlot slot = new PCLCardSlot(this, min, max);
        cardSlots.add(slot);

        return slot;
    }

    public PCLRelicSlot addRelicSlot()
    {
        final PCLRelicSlot slot = new PCLRelicSlot(this);
        relicSlots.add(slot);

        return slot;
    }

    public int cardsSize()
    {
        return cardSlots.size();
    }

    public PCLCardSlot getCardSlot(int index)
    {
        return cardSlots.get(index);
    }

    public PCLRelicSlot getRelicSlot(int index)
    {
        return relicSlots.get(index);
    }

    public PCLLoadoutData makeCopy()
    {
        return makeCopy(preset);
    }

    public PCLLoadoutData makeCopy(int preset)
    {
        final PCLLoadoutData copy = new PCLLoadoutData();
        copy.preset = preset;
        copy.values.putAll(values);
        for (PCLCardSlot slot : cardSlots)
        {
            copy.cardSlots.add(slot.makeCopy(copy));
        }
        for (PCLRelicSlot slot : relicSlots)
        {
            copy.relicSlots.add(slot.makeCopy(copy));
        }

        return copy;
    }

    public int relicsSize()
    {
        return relicSlots.size();
    }

    public PCLLoadoutValidation validate()
    {
        return PCLLoadoutValidation.createFrom(this);
    }

    public static class LoadoutInfo implements Serializable
    {
        public String loadout;
        public int preset;
        public String values;
        public String[] relics;
        public LoadoutCardInfo[] cards;

        public LoadoutInfo()
        {

        }

        public LoadoutInfo(String id, PCLLoadoutData data)
        {
            loadout = id;
            preset = data.preset;
            values = EUIUtils.serialize(data.values);
            cards = EUIUtils.arrayMap(data.cardSlots, LoadoutCardInfo.class, d -> d.selected != null ? new LoadoutCardInfo(d.selected.data.ID, d.amount) : null);
            relics = EUIUtils.arrayMap(data.relicSlots, String.class, d -> d.selected != null ? d.selected.relic.relicId : null);
        }

        public void fill(PCLLoadoutData data)
        {
            data.preset = preset;
            data.values.putAll(EUIUtils.deserialize(values, TValue.getType()));
            for (int i = 0; i < relics.length; i++)
            {
                data.getRelicSlot(i).select(relics[i]);
            }
            for (int i = 0; i < cards.length; i++)
            {
                if (cards[i] != null)
                {
                    data.getCardSlot(i).select(cards[i].id, cards[i].count);
                }
                else
                {
                    data.getCardSlot(i).select(null);
                }
            }
        }

        public static class LoadoutCardInfo implements Serializable
        {
            public String id;
            public Integer count;

            public LoadoutCardInfo(String id, Integer count)
            {
                this.id = id;
                this.count = count;
            }
        }
    }
}
