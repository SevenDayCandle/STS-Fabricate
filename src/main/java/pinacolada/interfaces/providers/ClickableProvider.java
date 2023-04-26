package pinacolada.interfaces.providers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.ui.tooltips.EUITooltip;

// Denotes object that can hold a PCLClickableUse, used in onClickableUsed
public interface ClickableProvider {
    default String getDescription() {
        return "";
    }

    String getID();

    default String getName() {
        return "";
    }

    default AbstractCreature getSource() {
        return AbstractDungeon.player;
    }

    EUITooltip getTooltip();

    default void onClicked() {
    }
}
