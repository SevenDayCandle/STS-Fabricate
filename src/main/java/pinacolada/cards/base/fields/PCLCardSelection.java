package pinacolada.cards.base.fields;

import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public enum PCLCardSelection {
    Manual,
    Top,
    Bottom,
    Random;

    public static <T> void addFirst(List<T> list, T item, int index) {
        list.add(Math.max(0, Math.min(list.size(), index)), item);
    }

    public static <T> void addLast(List<T> list, T item, int index) {
        list.add(Math.max(0, Math.min(list.size(), list.size() - index)), item);
    }

    public static <T> void addRandom(List<T> list, T item) {
        if (list.size() > 0) {
            list.add(GameUtilities.getRNG().random(list.size()), item);
        }
        else {
            list.add(item);
        }
    }

    public static <T> T getFirst(List<T> list, int index) {
        T card = null;
        if (index >= 0 && index < list.size()) {
            card = list.get(index);
        }

        return card;
    }

    public static <T> T getLast(List<T> list, int index) {
        T card = null;
        int position = list.size() - 1 - index;
        if (position >= 0 && position < list.size()) {
            card = list.get(position);
        }

        return card;
    }

    public static <T> T getRandom(List<T> list, int index) {
        T card = null;
        if (list.size() > 0) {
            int position = GameUtilities.getRNG().random(list.size() - 1);
            card = list.get(position);
            list.remove(position);
        }

        return card;
    }

    public <T> void add(List<T> list, T item, int index) {
        switch (this) {
            case Top:
                addLast(list, item, index);
                break;
            case Bottom:
                addFirst(list, item, index);
                break;
            case Random:
                addRandom(list, item);
        }
    }

    public <T> T get(List<T> list, int index) {
        switch (this) {
            case Top:
                return getLast(list, index);
            case Bottom:
                return getFirst(list, index);
            case Random:
                return getRandom(list, index);
        }
        return null;
    }

    // These strings cannot be put in as an enum variable because cards are initialized before these strings are
    public final String getTitle() {
        switch (this) {
            case Manual:
                return PGR.core.strings.cpile_manual;
            case Top:
                return PGR.core.strings.cpile_top;
            case Bottom:
                return PGR.core.strings.cpile_bottom;
            case Random:
                return PGR.core.strings.cpile_random;
        }
        return "";
    }

}
