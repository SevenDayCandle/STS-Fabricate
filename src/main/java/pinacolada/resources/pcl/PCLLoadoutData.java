package pinacolada.resources.pcl;

import com.google.gson.reflect.TypeToken;
import extendedui.EUIUtils;
import extendedui.utilities.TupleT2;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PCLLoadoutData
{
    private static final TypeToken<HashMap<PCLBaseStatEditor.StatType, Integer>> TValue = new TypeToken<HashMap<PCLBaseStatEditor.StatType, Integer>>() {};
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

    public PCLLoadout.Validation validate()
    {
        return PCLLoadout.Validation.For(this);
    }

    public static class LoadoutInfo implements Serializable
    {
        public String values;
        public String[] relics;
        public TupleT2<String, Integer>[] cards;

        public LoadoutInfo()
        {

        }

        public LoadoutInfo(PCLLoadoutData data)
        {
            values = EUIUtils.serialize(data.values);
            cards = EUIUtils.arrayMap(data.cardSlots, d -> new TupleT2<>(d.selected.data.ID, d.amount));
            relics = EUIUtils.arrayMap(data.relicSlots, d -> d.selected.relic.relicId);
        }

        public void fill(PCLLoadoutData data)
        {
            data.values.putAll(EUIUtils.deserialize(values, TValue.getType()));
            for (int i = 0; i < relics.length; i++)
            {
                data.getRelicSlot(i).select(relics[i]);
            }
            for (int i = 0; i < cards.length; i++)
            {
                data.getCardSlot(i).select(cards[i].v1, cards[i].v2);
            }
        }
    }
}
