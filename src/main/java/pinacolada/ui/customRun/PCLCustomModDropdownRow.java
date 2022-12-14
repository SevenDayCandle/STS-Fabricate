package pinacolada.ui.customRun;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUIDropdownRow;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;

// TODO Additional color filter
public class PCLCustomModDropdownRow extends EUIDropdownRow<CustomMod>
{
    private static Color getColorForColorString(String s)
    {
        switch (s)
        {
            case "b":
                return Settings.BLUE_TEXT_COLOR;
            case "g":
                return Settings.GREEN_TEXT_COLOR;
            case "p":
                return Settings.PURPLE_COLOR;
            case "r":
                return Settings.RED_TEXT_COLOR;
            case "y":
                return Settings.GOLD_COLOR;
        }
        return Settings.CREAM_COLOR;
    }

    private static String getCustomModDescription(CustomMod mod)
    {
        return mod.description;
    }

    private static String getCustomModLabel(CustomMod mod)
    {
        return mod.name;
    }

    protected final EUITooltip tooltip;
    protected final Color cForString;

    public PCLCustomModDropdownRow(EUIDropdown<CustomMod> dr, EUIHitbox hb, CustomMod item, int index)
    {
        super(dr, hb, item, index);
        cForString = getColorForColorString(item.color);
        this.label.setColor(cForString);
        this.tooltip = new EUITooltip(label.text, getCustomModDescription(item));
    }

    @Override
    public boolean update(boolean isInRange, boolean isSelected)
    {
        this.hb.update();
        this.label.updateImpl();
        this.isSelected = isSelected;
        if (!isInRange)
        {
            return false;
        }
        if (this.hb.hovered)
        {
            this.label.setColor(Color.WHITE);
            if (InputHelper.justClickedLeft)
            {
                this.hb.clickStarted = true;
            }
            EUITooltip.queueTooltip(tooltip, Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f);
        }
        else
        {
            this.label.setColor(cForString);
            this.checkbox.setTexture(isSelected ? ImageMaster.COLOR_TAB_BOX_TICKED : ImageMaster.COLOR_TAB_BOX_UNTICKED);
        }

        if ((this.hb.clicked) || (this.hb.hovered && CInputActionSet.select.isJustPressed()))
        {
            this.hb.clicked = false;
            this.checkbox.setTexture(isSelected ? ImageMaster.COLOR_TAB_BOX_UNTICKED : ImageMaster.COLOR_TAB_BOX_TICKED);
            return true;
        }
        return false;
    }
}
