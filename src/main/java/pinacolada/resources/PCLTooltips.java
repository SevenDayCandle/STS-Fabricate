package pinacolada.resources;

import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLAffinity;

public abstract class PCLTooltips
{
    abstract public void initializeIcons();
    public EUITooltip getLevelTooltip(PCLAffinity affinity)
    {
        return PGR.core.tooltips.level;
    }
    public EUITooltip getRerollTooltip()
    {
        return PGR.core.tooltips.reroll;
    }
}
