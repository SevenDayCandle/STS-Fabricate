package pinacolada.cards.base.fields;

import extendedui.EUIUtils;

import java.io.Serializable;

import static pinacolada.cards.base.fields.PCLAffinity.TOTAL_AFFINITIES;


public class PCLCardDataAffinityGroup implements Serializable
{
    public PCLCardDataAffinity star = null;
    protected PCLCardDataAffinity[] list = new PCLCardDataAffinity[TOTAL_AFFINITIES];

    public PCLCardDataAffinityGroup()
    {

    }

    public PCLCardDataAffinityGroup(PCLCardDataAffinityGroup other)
    {
        if (other.star != null)
        {
            star = other.star.makeCopy();
        }
        for (PCLCardDataAffinity a : other.list)
        {
            if (a != null)
            {
                list[a.type.id] = a.makeCopy();
            }
        }
    }

    public PCLCardDataAffinity get(PCLAffinity affinity)
    {
        if (affinity == PCLAffinity.Star)
        {
            return star;
        }

        if (affinity.id < 0 || affinity.id >= TOTAL_AFFINITIES)
        {
            return null;
        }

        return list[affinity.id];
    }

    public int getLevel(PCLAffinity affinity)
    {
        return getLevel(affinity, 0, true);
    }

    public int getLevel(PCLAffinity affinity, int form)
    {
        return getLevel(affinity, form, true);
    }

    public int getLevel(PCLAffinity affinity, int form, boolean useStarLevel)
    {
        int star = (this.star != null ? this.star.get(form) : 0);
        if (affinity == PCLAffinity.Star || (useStarLevel && star > 0))
        {
            return star;
        }
        else if (affinity == PCLAffinity.General)
        {
            final PCLCardDataAffinity a = EUIUtils.findMax(list, af -> af.get(form));
            return a == null ? (useStarLevel ? star : 0) : a.get(form); // Highest level among all affinities
        }
        else
        {
            final PCLCardDataAffinity a = get(affinity);
            return (a != null) ? a.get(form) : 0;
        }
    }

    public int getUpgrade(PCLAffinity affinity)
    {
        return getUpgrade(affinity, 0, true);
    }

    public int getUpgrade(PCLAffinity affinity, int form)
    {
        return getUpgrade(affinity, form, true);
    }

    public int getUpgrade(PCLAffinity affinity, int form, boolean useStarLevel)
    {
        int star = (this.star != null ? this.star.getUpgrade(form) : 0);
        if (affinity == PCLAffinity.Star || (useStarLevel && star > 0))
        {
            return star;
        }
        else if (affinity == PCLAffinity.General)
        {
            final PCLCardDataAffinity a = EUIUtils.findMax(list, af -> af.getUpgrade(form));
            return a == null ? (useStarLevel ? star : 0) : a.getUpgrade(form); // Highest level among all affinities
        }
        else
        {
            final PCLCardDataAffinity a = get(affinity);
            return (a != null) ? a.getUpgrade(form) : 0;
        }
    }

    public PCLCardDataAffinity set(PCLCardDataAffinity affinity)
    {
        if (affinity.type.id < 0)
        {
            star = affinity;
        }
        else
        {
            list[affinity.type.id] = affinity;
        }
        return affinity;
    }

    public PCLCardDataAffinity set(PCLAffinity affinity, int level)
    {
        return set(new PCLCardDataAffinity(affinity, level));
    }

    public PCLCardDataAffinity set(PCLAffinity affinity, int level, int upgrade)
    {
        return set(new PCLCardDataAffinity(affinity, level, upgrade));
    }

    public PCLCardDataAffinity set(PCLAffinity affinity, int level, Integer[] upgrade)
    {
        return set(new PCLCardDataAffinity(affinity, level, upgrade));
    }

    public PCLCardDataAffinity set(PCLAffinity affinity, Integer[] level, Integer[] upgrade)
    {
        return set(new PCLCardDataAffinity(affinity, level, upgrade));
    }
}