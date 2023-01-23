package pinacolada.cards.base.fields;

import java.io.Serializable;

import static extendedui.EUIUtils.array;
import static extendedui.EUIUtils.safeIndex;

public class PCLCardDataAffinity implements Serializable
{
    public final PCLAffinity type;
    public Integer[] value;
    public Integer[] upgrades;

    public PCLCardDataAffinity(PCLAffinity type, Integer value)
    {
        this(type, array(value), null);
    }

    public PCLCardDataAffinity(PCLAffinity type, Integer value, Integer upgrade)
    {
        this(type, array(value), array(upgrade));
    }

    public PCLCardDataAffinity(PCLAffinity type, Integer value, Integer[] upgrades)
    {
        this(type, array(value), upgrades);
    }

    public PCLCardDataAffinity(PCLAffinity type, Integer[] values, Integer[] upgrades)
    {
        this.type = type;
        value = values;
        this.upgrades = upgrades;
    }

    public int get(int form)
    {
        Integer value = safeIndex(this.value, form);
        return value != null ? value : 0;
    }

    public int getUpgrade(int form)
    {
        if (upgrades == null)
        {
            return 0;
        }
        Integer value = safeIndex(upgrades, form);
        return value != null ? value : 0;
    }

    public PCLCardDataAffinity makeCopy()
    {
        return new PCLCardDataAffinity(type, value, upgrades);
    }
}