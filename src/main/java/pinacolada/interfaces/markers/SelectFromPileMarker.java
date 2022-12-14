package pinacolada.interfaces.markers;

import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.SelectFromPile;

public interface SelectFromPileMarker
{
    FuncT3<SelectFromPile, String, Integer, CardGroup[]> getAction();

    EUITooltip getActionTooltip();

    default String tooltipPast()
    {
        return getActionTooltip().past();
    }

    default String tooltipTitle()
    {
        return getActionTooltip().title;
    }
}
