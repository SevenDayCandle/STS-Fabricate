package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUIDropdownRow;
import extendedui.ui.controls.EUITextBoxNumericalInput;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCardTagInfo;

import static pinacolada.ui.cardEditor.PCLCustomCardAttributesPage.MENU_HEIGHT;

public class PCLCustomCardTagEditorRow extends EUIDropdownRow<PCLCardTagInfo>
{
    public static final float ICON_SIZE = 32f * Settings.scale;
    protected static final int MIN_LEVEL = -1;
    protected static final int MAX_LEVEL = 2;

    protected int form;
    protected EUITextBoxNumericalInput displayValue;
    protected EUITextBoxNumericalInput displayValue2;
    protected EUIButton decreaseButton;
    protected EUIButton decreaseButton2;
    protected EUIButton increaseButton;
    protected EUIButton increaseButton2;

    public PCLCustomCardTagEditorRow(EUIDropdown<PCLCardTagInfo> dr, EUIHitbox hb, PCLCardTagInfo item, int index)
    {
        super(dr, hb, item, index);

        displayValue = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(),
                new RelativeHitbox(hb, MENU_HEIGHT, MENU_HEIGHT, MENU_HEIGHT * 6, MENU_HEIGHT * 0.5f).setIsPopupCompatible(true).setParentElement(dr))
                .showNegativeAsInfinity(true)
                .setLimits(MIN_LEVEL, MAX_LEVEL)
                .setOnComplete(v -> {
                    // TODO use stack for active elements in EUI
                    EUI.setActiveElement(dr);
                    this.setPrimary(v, true);
                })
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);
        displayValue.setValue(1);

        displayValue2 = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(),
                new RelativeHitbox(displayValue.hb, displayValue.hb.width, displayValue.hb.height, MENU_HEIGHT * 3.2f, MENU_HEIGHT * 0.5f).setIsPopupCompatible(true).setParentElement(dr))
                .showNegativeAsInfinity(true)
                .setLimits(MIN_LEVEL, MAX_LEVEL)
                .setOnComplete(v -> {
                    EUI.setActiveElement(dr);
                    this.setSecondary(v, true);
                })
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);
        displayValue2.setValue(1);

        decreaseButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(displayValue.hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.4f, MENU_HEIGHT * 0.5f).setIsPopupCompatible(true).setParentElement(dr))
                .setOnClick(this::decreasePrimary)
                .setText(null);

        decreaseButton2 = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(displayValue.hb, ICON_SIZE, ICON_SIZE, 2.7f * MENU_HEIGHT + ICON_SIZE * -0.4f, MENU_HEIGHT * 0.5f).setIsPopupCompatible(true).setParentElement(dr))
                .setOnClick(this::decreaseSecondary)
                .setText(null);

        increaseButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(displayValue.hb, ICON_SIZE, ICON_SIZE, MENU_HEIGHT + (ICON_SIZE * 0.4f), MENU_HEIGHT * 0.5f).setIsPopupCompatible(true).setParentElement(dr))
                .setOnClick(this::increasePrimary)
                .setText(null);

        increaseButton2 = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(displayValue.hb, ICON_SIZE, ICON_SIZE, 3.7f * MENU_HEIGHT + (ICON_SIZE * 0.4f), MENU_HEIGHT * 0.5f).setIsPopupCompatible(true).setParentElement(dr))
                .setOnClick(this::increaseSecondary)
                .setText(null);
    }

    public void decreasePrimary()
    {
        setPrimary(displayValue.getCachedValue() - 1, true);
    }

    public void decreaseSecondary()
    {
        setSecondary( displayValue2.getCachedValue() - 1, true);
    }

    public int getValue()
    {
        return displayValue.getCachedValue();
    }

    public void increasePrimary()
    {
        setPrimary(displayValue.getCachedValue() + 1, true);
    }

    public void increaseSecondary()
    {
        setSecondary(displayValue2.getCachedValue() + 1, true);
    }

    public PCLCustomCardTagEditorRow setPrimary(int value, boolean update)
    {
        displayValue.setValue(value);
        if (update)
        {
            item.set(form, displayValue.getCachedValue());
            dr.updateForSelection(true);
        }
        return this;
    }

    public PCLCustomCardTagEditorRow setSecondary(int value2, boolean update)
    {
        displayValue2.setValue(value2);
        if (update)
        {
            item.setUpgrade(form, displayValue2.getCachedValue());
            dr.updateForSelection(true);
        }
        return this;
    }

    public PCLCustomCardTagEditorRow setValue(int value, int valueSecondary, boolean update)
    {
        displayValue.setValue(value);
        displayValue2.setValue(valueSecondary);
        if (update)
        {
            item.set(form, displayValue.getCachedValue());
            item.setUpgrade(form, displayValue2.getCachedValue());
            dr.updateForSelection(true);
        }

        return this;
    }

    public boolean update(boolean isInRange, boolean isSelected)
    {
        this.hb.update();
        this.label.updateImpl();
        this.checkbox.updateImpl();
        this.isSelected = isSelected;
        if (isSelected)
        {
            decreaseButton.setInteractable(displayValue.getCachedValue() > MIN_LEVEL).updateImpl();
            decreaseButton2.setInteractable(displayValue2.getCachedValue() > MIN_LEVEL).updateImpl();
            increaseButton.setInteractable(displayValue.getCachedValue() < MAX_LEVEL).updateImpl();
            increaseButton2.setInteractable(displayValue2.getCachedValue() < MAX_LEVEL).updateImpl();
            displayValue.updateImpl();
            displayValue2.updateImpl();
        }
        if (!isInRange)
        {
            return false;
        }
        return tryHover(isSelected);
    }

    protected boolean tryHover(boolean isSelected)
    {
        if (!isComponentHovered() && this.hb.hovered)
        {
            this.label.setColor(Settings.GREEN_TEXT_COLOR);
            if (InputHelper.justClickedLeft)
            {
                this.hb.clickStarted = true;
            }
            if (dr.showTooltipOnHover)
            {
                addTooltip();
            }
        }
        else if (isSelected)
        {
            this.label.setColor(Settings.GOLD_COLOR);
            this.checkbox.setTexture(ImageMaster.COLOR_TAB_BOX_TICKED);
        }
        else
        {
            this.label.setColor(Color.WHITE);
            this.checkbox.setTexture(ImageMaster.COLOR_TAB_BOX_UNTICKED);
        }

        if (((this.hb.clicked) || (this.hb.hovered && CInputActionSet.select.isJustPressed()) && EUI.tryClick(this.hb)))
        {
            this.hb.clicked = false;
            this.checkbox.setTexture(isSelected ? ImageMaster.COLOR_TAB_BOX_UNTICKED : ImageMaster.COLOR_TAB_BOX_TICKED);
            return true;
        }
        return false;
    }

    public void renderRow(SpriteBatch sb)
    {
        super.renderRow(sb);
        if (isSelected)
        {
            decreaseButton2.tryRenderCentered(sb);
            increaseButton2.tryRenderCentered(sb);
            decreaseButton.tryRenderCentered(sb);
            increaseButton.tryRenderCentered(sb);
            displayValue2.tryRender(sb);
            displayValue.tryRender(sb);
        }
    }

    protected boolean isComponentHovered()
    {
        return decreaseButton2.hb.hovered || increaseButton2.hb.hovered || decreaseButton.hb.hovered || increaseButton.hb.hovered || displayValue2.hb.hovered || displayValue.hb.hovered;
    }
}
