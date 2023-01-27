package pinacolada.resources.loadout;

import extendedui.EUIUtils;
import extendedui.utilities.TupleT2;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;

import java.util.HashMap;

public class PCLLoadoutValidation
{
    public final TupleT2<Integer, Boolean> cardsCount = new TupleT2<>();
    public final TupleT2<Integer, Boolean> totalValue = new TupleT2<>();
    public final HashMap<PCLBaseStatEditor.StatType, Integer> values = new HashMap<>();
    public int hindranceLevel;
    public boolean allCardsSeen;
    public boolean isValid;

    public PCLLoadoutValidation()
    {

    }

    public PCLLoadoutValidation(PCLLoadoutData data)
    {
        refresh(data);
    }

    public static PCLLoadoutValidation For(PCLLoadoutData data)
    {
        return new PCLLoadoutValidation(data);
    }

    public PCLLoadoutValidation refresh(PCLLoadoutData data)
    {
        if (data == null || data.preset < 0 || data.preset >= PCLLoadout.MAX_PRESETS)
        {
            isValid = false;
            return this;
        }

        cardsCount.set(0, false);
        totalValue.set(PCLLoadout.MAX_VALUE, false);
        allCardsSeen = true;
        int weakHindrances = 0;
        int strongHindrances = 0;
        for (PCLCardSlot slot : data.cardSlots)
        {
            if (slot == null)
            {
                continue;
            }

            totalValue.v1 += slot.getEstimatedValue();
            cardsCount.v1 += slot.amount;

            if (slot.selected != null)
            {
                if (slot.selected.data.isNotSeen())
                {
                    allCardsSeen = false;
                }

                if (slot.selected.estimatedValue < -2)
                {
                    strongHindrances += slot.amount;
                }
                else if (slot.selected.estimatedValue < 0)
                {
                    weakHindrances += slot.amount;
                }
            }
        }
        for (PCLRelicSlot slot : data.relicSlots)
        {
            if (slot == null)
            {
                continue;
            }

            totalValue.v1 += slot.getEstimatedValue();
        }

        // Hindrance level is determined by the proportion of your deck that is "bad"
        // Strikes/Defends and harmless hindrances have a weaker influence
        // Curses and damaging hindrances have a stronger influence
        if (cardsCount.v1 > 0)
        {
            int strongHindranceLevel = (int) Math.max(0, (30 * Math.pow(strongHindrances, 1.5) / cardsCount.v1) - 7);
            int weakHindranceLevel = Math.max(0, (30 * (weakHindrances + strongHindrances) / cardsCount.v1) - 20);
            hindranceLevel = -strongHindranceLevel - weakHindranceLevel;
        }
        else
        {
            hindranceLevel = 0;
        }

        values.putAll(data.values);
        totalValue.v1 += (int) EUIUtils.sum(values.values(), Float::valueOf) + hindranceLevel;
        totalValue.v2 = totalValue.v1 <= PCLLoadout.MAX_VALUE;
        cardsCount.v2 = cardsCount.v1 >= PCLLoadout.MIN_CARDS;
        isValid = totalValue.v2 && cardsCount.v2 && allCardsSeen;

        return this;
    }
}
