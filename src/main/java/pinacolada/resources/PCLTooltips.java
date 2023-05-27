package pinacolada.resources;

import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.cards.base.fields.PCLAffinity;

public abstract class PCLTooltips {
    public EUIKeywordTooltip getLevelTooltip(PCLAffinity affinity) {
        return affinity.getTooltip();
    }

    abstract public void initializeIcons();
}
