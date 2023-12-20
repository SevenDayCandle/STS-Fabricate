package pinacolada.cards.base.fields;

import extendedui.EUIUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class PCLCardDataAffinityGroup implements Serializable {
    static final long serialVersionUID = 1L;
    protected PCLCardDataAffinity[] list = new PCLCardDataAffinity[PCLAffinity.getCount()];
    public PCLCardDataAffinity star = null;

    public PCLCardDataAffinityGroup() {

    }

    public PCLCardDataAffinityGroup(PCLCardDataAffinityGroup other) {
        if (other.star != null) {
            star = other.star.makeCopy();
        }
        for (PCLCardDataAffinity a : other.list) {
            if (a != null) {
                list[a.type.ID] = a.makeCopy();
            }
        }
    }

    public PCLCardDataAffinity get(PCLAffinity affinity) {
        if (affinity == PCLAffinity.Star) {
            return star;
        }

        if (affinity.ID < 0 || affinity.ID >= PCLAffinity.getCount()) {
            return null;
        }

        return list[affinity.ID];
    }

    public ArrayList<PCLAffinity> getAffinities() {
        return getAffinities(true, false);
    }

    public ArrayList<PCLAffinity> getAffinities(boolean convertStar, boolean availableOnly) {
        HashSet<PCLAffinity> available = new HashSet<>(availableOnly ? PCLAffinity.getAvailableAffinities() : PCLAffinity.basic());
        final ArrayList<PCLAffinity> list = new ArrayList<>();
        if (convertStar && hasStar()) {
            list.addAll(available);
        }
        else {
            for (PCLCardDataAffinity item : this.list) {
                if (item != null && item.value.length > 0 && available.contains(item.type)) {
                    list.add(item.type);
                }
            }
            if (hasStar()) {
                list.add(star.type);
            }
        }
        return list;
    }

    public ArrayList<PCLCardDataAffinity> getCardAffinities() {
        return getCardAffinities(0, true);
    }

    public ArrayList<PCLCardDataAffinity> getCardAffinities(int form, boolean filterLevelZero) {
        final ArrayList<PCLCardDataAffinity> list = new ArrayList<>();
        for (PCLCardDataAffinity item : this.list) {
            if (item != null && (!filterLevelZero || item.get(form) > 0)) {
                list.add(item);
            }
        }
        if (star != null && (!filterLevelZero || star.get(form) > 0)) {
            list.add(star);
        }
        return list;
    }

    public int getLevel(PCLAffinity affinity) {
        return getLevel(affinity, 0, true);
    }

    public int getLevel(PCLAffinity affinity, int form, boolean useStarLevel) {
        int star = (this.star != null ? this.star.get(form) : 0);
        if (affinity == PCLAffinity.Star || (useStarLevel && star > 0)) {
            return star;
        }
        else if (affinity == PCLAffinity.General) {
            final PCLCardDataAffinity a = EUIUtils.findMax(list, af -> af.get(form));
            return a == null ? (useStarLevel ? star : 0) : a.get(form); // Highest level among all affinities
        }
        else {
            final PCLCardDataAffinity a = get(affinity);
            return (a != null) ? a.get(form) : 0;
        }
    }

    public int getLevel(PCLAffinity affinity, int form) {
        return getLevel(affinity, form, true);
    }

    public int getUpgrade(PCLAffinity affinity) {
        return getUpgrade(affinity, 0, true);
    }

    public int getUpgrade(PCLAffinity affinity, int form, boolean useStarLevel) {
        int star = (this.star != null ? this.star.getUpgrade(form) : 0);
        if (affinity == PCLAffinity.Star || (useStarLevel && star > 0)) {
            return star;
        }
        else if (affinity == PCLAffinity.General) {
            final PCLCardDataAffinity a = EUIUtils.findMax(list, af -> af.getUpgrade(form));
            return a == null ? (useStarLevel ? star : 0) : a.getUpgrade(form); // Highest level among all affinities
        }
        else {
            final PCLCardDataAffinity a = get(affinity);
            return (a != null) ? a.getUpgrade(form) : 0;
        }
    }

    public int getUpgrade(PCLAffinity affinity, int form) {
        return getUpgrade(affinity, form, true);
    }

    public boolean hasStar() {
        return star != null && star.value.length > 0;
    }

    public PCLCardDataAffinity set(PCLAffinity affinity, int level) {
        return set(new PCLCardDataAffinity(affinity, level));
    }

    public PCLCardDataAffinity set(PCLCardDataAffinity affinity) {
        if (affinity.type.ID < 0) {
            star = affinity;
        }
        else {
            if (affinity.type.ID >= list.length) {
                list = Arrays.copyOf(list, affinity.type.ID + 1);
            }
            list[affinity.type.ID] = affinity;
        }
        return affinity;
    }

    public PCLCardDataAffinity set(PCLAffinity affinity, int level, int upgrade) {
        return set(new PCLCardDataAffinity(affinity, level, upgrade));
    }

    public PCLCardDataAffinity set(PCLAffinity affinity, int level, Integer[] upgrade) {
        return set(new PCLCardDataAffinity(affinity, level, upgrade));
    }

    public PCLCardDataAffinity set(PCLAffinity affinity, Integer[] level, Integer[] upgrade) {
        return set(new PCLCardDataAffinity(affinity, level, upgrade));
    }
}