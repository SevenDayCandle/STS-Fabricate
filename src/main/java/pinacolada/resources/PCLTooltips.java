package pinacolada.resources;

import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.fields.PCLAffinity;

public abstract class PCLTooltips
{
    abstract public void initializeIcons();
    public EUITooltip getLevelTooltip(PCLAffinity affinity)
    {
        return affinity.getTooltip();
    }
    public EUITooltip getRerollTooltip()
    {
        return PGR.core.tooltips.reroll;
    }
}
