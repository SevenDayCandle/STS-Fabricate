package pinacolada.interfaces.markers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.SelectFromPile;

public interface SelectFromPileMarker
{
    FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction();

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
