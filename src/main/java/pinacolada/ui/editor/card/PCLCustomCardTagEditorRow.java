package pinacolada.ui.editor.card;

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
import pinacolada.cards.base.fields.PCLCardTagInfo;

import static pinacolada.ui.editor.card.PCLCustomCardAttributesPage.MENU_HEIGHT;

public class PCLCustomCardTagEditorRow extends EUIDropdownRow<PCLCardTagInfo> {
    public static final float ICON_SIZE = 24f * Settings.scale;

    protected int form;
    protected EUITextBoxNumericalInput displayValue;
    protected EUITextBoxNumericalInput displayValue2;
    protected EUIButton decreaseButton;
    protected EUIButton decreaseButton2;
    protected EUIButton increaseButton;
    protected EUIButton increaseButton2;

    public PCLCustomCardTagEditorRow(EUIDropdown<PCLCardTagInfo> dr, EUIHitbox hb, PCLCardTagInfo item, int index) {
        super(dr, hb, item, index);

        // Tag limits are copied from the extra limits for PSkills
        displayValue = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(),
                new RelativeHitbox(hb, MENU_HEIGHT, MENU_HEIGHT, MENU_HEIGHT * 5.2f, MENU_HEIGHT * 0.25f).setIsPopupCompatible(true).setParentElement(dr))
                .showNegativeAsInfinity(true)
                .setLimits(item.tag.minValue, item.tag.maxValue)
                .setOnComplete(v -> {
                    // TODO use stack for active elements in EUI
                    EUI.setActiveElement(dr);
                    this.setPrimary(v, true);
                })
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

        displayValue2 = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(),
                new RelativeHitbox(hb, MENU_HEIGHT, MENU_HEIGHT, MENU_HEIGHT * 7.9f, MENU_HEIGHT * 0.25f).setIsPopupCompatible(true).setParentElement(dr))
                .showNegativeAsInfinity(true)
                .setLimits(item.tag.minValue, item.tag.maxValue)
                .setOnComplete(v -> {
                    EUI.setActiveElement(dr);
                    this.setSecondary(v, true);
                })
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

        decreaseButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(displayValue.hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.4f, MENU_HEIGHT * 0.5f).setIsPopupCompatible(true).setParentElement(dr))
                .setOnClick(this::decreasePrimary);

        decreaseButton2 = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(displayValue2.hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.4f, MENU_HEIGHT * 0.5f).setIsPopupCompatible(true).setParentElement(dr))
                .setOnClick(this::decreaseSecondary);

        increaseButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(displayValue.hb, ICON_SIZE, ICON_SIZE, MENU_HEIGHT + (ICON_SIZE * 0.4f), MENU_HEIGHT * 0.5f).setIsPopupCompatible(true).setParentElement(dr))
                .setOnClick(this::increasePrimary);

        increaseButton2 = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(displayValue2.hb, ICON_SIZE, ICON_SIZE, MENU_HEIGHT + (ICON_SIZE * 0.4f), MENU_HEIGHT * 0.5f).setIsPopupCompatible(true).setParentElement(dr))
                .setOnClick(this::increaseSecondary);

        forceRefresh();
    }

    public void decreasePrimary() {
        setPrimary(displayValue.getCachedValue() - 1, true);
    }

    public void decreaseSecondary() {
        setSecondary(displayValue2.getCachedValue() - 1, true);
    }

    public void forceRefresh() {
        displayValue.forceSetValue(item.get(form), false);
        displayValue2.forceSetValue(item.getUpgrade(form), false);
    }

    public int getValue() {
        return displayValue.getCachedValue();
    }

    public void increasePrimary() {
        setPrimary(displayValue.getCachedValue() + 1, true);
    }

    public void increaseSecondary() {
        setSecondary(displayValue2.getCachedValue() + 1, true);
    }

    protected boolean isComponentHovered() {
        return decreaseButton2.hb.hovered || increaseButton2.hb.hovered || decreaseButton.hb.hovered || increaseButton.hb.hovered || displayValue2.hb.hovered || displayValue.hb.hovered;
    }

    public void renderRow(SpriteBatch sb) {
        super.renderRow(sb);
        if (isSelected) {
            decreaseButton2.tryRenderCentered(sb);
            increaseButton2.tryRenderCentered(sb);
            decreaseButton.tryRenderCentered(sb);
            increaseButton.tryRenderCentered(sb);
            displayValue2.tryRender(sb);
            displayValue.tryRender(sb);
        }
    }

    public PCLCustomCardTagEditorRow setForm(int form) {
        this.form = form;
        return this;
    }

    public PCLCustomCardTagEditorRow setPrimary(int value, boolean update) {
        displayValue.forceSetValue(value, false);
        if (update) {
            item.set(form, displayValue.getCachedValue());
            dr.updateForSelection(true);
        }
        return this;
    }

    public PCLCustomCardTagEditorRow setSecondary(int value2, boolean update) {
        displayValue2.forceSetValue(value2, false);
        if (update) {
            item.setUpgrade(form, displayValue2.getCachedValue());
            dr.updateForSelection(true);
        }
        return this;
    }

    public PCLCustomCardTagEditorRow setValue(int value, int valueSecondary, boolean update) {
        displayValue.forceSetValue(value, false);
        displayValue2.forceSetValue(valueSecondary, false);
        if (update) {
            item.set(form, displayValue.getCachedValue());
            item.setUpgrade(form, displayValue2.getCachedValue());
            dr.updateForSelection(true);
        }

        return this;
    }

    protected boolean tryHover(boolean isSelected) {
        if (!isComponentHovered() && this.hb.hovered) {
            this.label.setColor(Settings.GREEN_TEXT_COLOR);
            if (InputHelper.justClickedLeft) {
                this.hb.clickStarted = true;
            }
            if (dr.showTooltipOnHover) {
                addTooltip();
            }
        }
        else if (isSelected) {
            this.label.setColor(Settings.GOLD_COLOR);
            this.checkbox.setTexture(ImageMaster.COLOR_TAB_BOX_TICKED);
        }
        else {
            this.label.setColor(Color.WHITE);
            this.checkbox.setTexture(ImageMaster.COLOR_TAB_BOX_UNTICKED);
        }

        if (((this.hb.clicked) || (this.hb.hovered && CInputActionSet.select.isJustPressed()) && EUI.tryClick(this.hb))) {
            this.hb.clicked = false;
            this.checkbox.setTexture(isSelected ? ImageMaster.COLOR_TAB_BOX_UNTICKED : ImageMaster.COLOR_TAB_BOX_TICKED);
            return true;
        }
        return false;
    }

    public boolean update(boolean isInRange, boolean isSelected) {
        this.hb.update();
        this.label.updateImpl();
        this.checkbox.updateImpl();
        this.isSelected = isSelected;
        if (isSelected) {
            decreaseButton.setInteractable(displayValue.getCachedValue() > displayValue.getMin()).updateImpl();
            decreaseButton2.setInteractable(displayValue2.getCachedValue() > displayValue.getMin()).updateImpl();
            increaseButton.setInteractable(displayValue.getCachedValue() < displayValue.getMax()).updateImpl();
            increaseButton2.setInteractable(displayValue2.getCachedValue() < displayValue.getMax()).updateImpl();
            displayValue.updateImpl();
            displayValue2.updateImpl();
        }
        if (!isInRange) {
            return false;
        }
        return tryHover(isSelected);
    }
}
