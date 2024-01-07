package pinacolada.resources.loadout;

import extendedui.EUIUtils;
import extendedui.utilities.TupleT2;
import pinacolada.resources.PGR;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;

import java.util.HashMap;
import java.util.StringJoiner;

public class PCLLoadoutValidation {
    public static final int HINDRANCE_MULTIPLIER = 20;
    public final TupleT2<Integer, Boolean> cardsCount = new TupleT2<>();
    public final TupleT2<Integer, Boolean> totalValue = new TupleT2<>();
    public final HashMap<PCLBaseStatEditor.StatType, Integer> values = new HashMap<>();
    public boolean allCardsSeen;
    public boolean isValid;

    public PCLLoadoutValidation() {

    }

    public PCLLoadoutValidation(PCLLoadoutData data) {
        refresh(data);
    }

    public static PCLLoadoutValidation createFrom(PCLLoadoutData data) {
        return new PCLLoadoutValidation(data);
    }

    public String getFailingString() {
        StringJoiner sj = new StringJoiner(EUIUtils.SPLIT_LINE);

        if (!totalValue.v2) {
            sj.add(PGR.core.strings.loadout_invalidLoadoutDescLimit);
        }
        if (!cardsCount.v2) {
            sj.add(PGR.core.strings.loadout_invalidLoadoutDescNotEnough);
        }
        if (!allCardsSeen) {
            sj.add(PGR.core.strings.loadout_invalidLoadoutDescSeen);
        }

        return sj.toString();
    }

    public PCLLoadoutValidation refresh(PCLLoadoutData data) {
        if (data == null || data.ID == null) {
            isValid = false;
            return this;
        }

        cardsCount.set(0, false);
        totalValue.set(0, false);
        allCardsSeen = true;
        int hindrances = 0;
        for (LoadoutCardSlot slot : data.cardSlots) {
            if (slot == null) {
                continue;
            }

            totalValue.v1 += slot.getEstimatedValue();
            cardsCount.v1 += slot.amount;

            if (slot.selected != null) {
                if (slot.isBanned() || slot.isLocked()) {
                    allCardsSeen = false;
                }

                else if (slot.getEstimatedValue() < 0) {
                    hindrances += slot.amount;
                }
            }
        }
        for (LoadoutRelicSlot slot : data.relicSlots) {
            if (slot == null) {
                continue;
            }

            totalValue.v1 += slot.getEstimatedValue();
        }

        values.putAll(data.values);
        totalValue.v1 += (int) EUIUtils.sum(values.values(), Float::valueOf);
        totalValue.v2 = data.loadout.maxValue < 0 || totalValue.v1 <= data.loadout.maxValue;
        cardsCount.v2 = cardsCount.v1 >= data.loadout.minTotalCards;
        isValid = totalValue.v2 && cardsCount.v2 && allCardsSeen;

        return this;
    }
}
