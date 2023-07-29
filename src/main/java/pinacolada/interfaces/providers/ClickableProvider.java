package pinacolada.interfaces.providers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.powers.PCLClickableUse;

// Denotes object that can hold a PCLClickableUse, used in onClickableUsed
public interface ClickableProvider {
    default String getDescription() {
        return "";
    }

    default String getName() {
        return "";
    }

    default AbstractCreature getSource() {
        return AbstractDungeon.player;
    }

    default void onClicked() {
    }

    String getID();
    EUITooltip getTooltip();
    PCLClickableUse getClickable();
}
