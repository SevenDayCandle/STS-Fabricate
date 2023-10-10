package pinacolada.resources;

import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.resources.pcl.PCLCoreTooltips;

public abstract class AbstractTooltips {

    protected static EUIKeywordTooltip tryLoadTip(String id) {
        EUIKeywordTooltip tip = EUIKeywordTooltip.findByID(id);
        if (tip != null) {
            return tip;
        }
        // Some computer locales may replace I with dotless i
        tip = EUIKeywordTooltip.findByName(id.toLowerCase());
        if (tip != null) {
            EUIUtils.logError(PCLCoreTooltips.class, "Tooltip found with name " + id + " but actual ID was " + tip.ID);
            EUIKeywordTooltip.registerID(id, tip);
            return tip;
        }
        EUIUtils.logError(PCLCoreTooltips.class, "EUI never found tooltip " + id);
        tip = new EUIKeywordTooltip(id);
        EUIKeywordTooltip.registerID(id, tip);
        return tip;
    }

    abstract public void initializeIcons();
}
