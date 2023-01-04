package pinacolada.interfaces.markers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.ui.tooltips.EUITooltip;

// Denotes object that can hold a PCLClickableUse, used in onClickableUsed
public interface ClickableProvider
{
    abstract String getID();
    abstract EUITooltip getTooltip();

    default AbstractCreature getSource()
    {
        return AbstractDungeon.player;
    }

    default String getDescription()
    {
        return "";
    }

    default String getName()
    {
        return "";
    }

    default void onClicked()
    {
    }
}
