package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUITextBoxNumericalInput;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

public class PCLCustomUpgradableEditor extends EUIHoverable {
    protected static final float ICON_SIZE = 36f * Settings.scale;

    protected ActionT2<Integer, Integer> onUpdate;
    protected EUIButton decreaseButton2;
    protected EUIButton decreaseButton;
    protected EUIButton increaseButton2;
    protected EUIButton increaseButton;
    protected EUILabel header;
    protected EUITextBoxNumericalInput displayValue;
    protected EUITextBoxNumericalInput displayValueSecondary;

    public PCLCustomUpgradableEditor(EUIHitbox hb, String title, ActionT2<Integer, Integer> onUpdate) {
        super(hb);

        final float w = hb.width;
        final float h = hb.height;

        decreaseButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.4f, h * 0.5f))
                .setOnClick(this::decreasePrimary);

        decreaseButton2 = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.4f, -h * 0.35f))
                .setOnClick(this::decreaseSecondary);

        increaseButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, w + (ICON_SIZE * 0.4f), h * 0.5f))
                .setOnClick(this::increasePrimary);

        increaseButton2 = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, w + (ICON_SIZE * 0.4f), -h * 0.35f))
                .setOnClick(this::increaseSecondary);

        displayValue = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), hb)
                .setOnComplete(this::setValue)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

        displayValueSecondary = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(),
                new RelativeHitbox(hb, hb.width, hb.height, w * 0.5f, -h * 0.35f))
                .setOnComplete(this::setSecondaryValue)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

        this.header = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new RelativeHitbox(hb, w, h, 0, hb.height * 1.2f))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.8f).setColor(Settings.GOLD_COLOR)
                .setLabel(title);

        this.onUpdate = onUpdate;
    }

    public void decreasePrimary() {
        setValue(displayValue.getCachedValue() - 1, displayValueSecondary.getCachedValue());
    }

    public void decreaseSecondary() {
        setValue(displayValue.getCachedValue(), displayValueSecondary.getCachedValue() - 1);
    }

    public int getValue() {
        return displayValue.getCachedValue();
    }

    public void increasePrimary() {
        setValue(displayValue.getCachedValue() + 1, displayValueSecondary.getCachedValue());
    }

    public void increaseSecondary() {
        setValue(displayValue.getCachedValue(), displayValueSecondary.getCachedValue() + 1);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        this.hb.render(sb);
        decreaseButton2.tryRender(sb);
        increaseButton2.tryRender(sb);
        decreaseButton.tryRender(sb);
        increaseButton.tryRender(sb);
        displayValueSecondary.tryRender(sb);
        displayValue.tryRender(sb);
        header.tryRender(sb);
    }

    public PCLCustomUpgradableEditor setHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return setHeader(font, fontScale, textColor, text, false);
    }

    public PCLCustomUpgradableEditor setHeader(BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);

        return this;
    }

    public PCLCustomUpgradableEditor setHeader(float x, float y, BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setPosition(x, y)
                .setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);

        return this;
    }

    public PCLCustomUpgradableEditor setHeaderText(String text) {
        this.header.setLabel(text);
        return this;
    }

    public PCLCustomUpgradableEditor setLimits(int minimum, int maximum) {
        displayValue.setLimits(minimum, maximum);

        return this;
    }

    public PCLCustomUpgradableEditor setSecondaryValue(int valueSecondary) {
        return setValue(displayValue.getCachedValue(), valueSecondary);
    }

    public PCLCustomUpgradableEditor setTooltip(String name, String desc) {
        super.setTooltip(name, desc);
        header.setTooltip(this.tooltip);
        return this;
    }

    public PCLCustomUpgradableEditor setTooltip(EUITooltip tip) {
        super.setTooltip(tip);
        header.setTooltip(tip);
        return this;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        decreaseButton.setInteractable(displayValue.getCachedValue() > displayValue.getMin()).tryUpdate();
        decreaseButton2.setInteractable(displayValue.getCachedValue() + displayValueSecondary.getCachedValue() > displayValue.getMin()).tryUpdate();
        increaseButton.setInteractable(displayValue.getCachedValue() < displayValue.getMax()).tryUpdate();
        increaseButton2.setInteractable(displayValue.getCachedValue() + displayValueSecondary.getCachedValue() < displayValue.getMax()).tryUpdate();
        displayValue.tryUpdate();
        displayValueSecondary.tryUpdate();
        header.tryUpdate();
    }

    public PCLCustomUpgradableEditor setValue(int value) {
        return setValue(value, displayValueSecondary.getCachedValue());
    }

    public PCLCustomUpgradableEditor setValue(int value, int valueSecondary) {
        return setValue(value, valueSecondary, true);
    }

    public PCLCustomUpgradableEditor setValue(int value, int valueSecondary, boolean invoke) {
        displayValue.forceSetValue(value, false);
        displayValueSecondary.forceSetValue(MathUtils.clamp(valueSecondary, displayValue.getMin() - displayValue.getCachedValue(), displayValue.getMax() - displayValue.getCachedValue()), false);
        if (invoke) {
            onUpdate.invoke(displayValue.getCachedValue(), displayValueSecondary.getCachedValue());
        }

        return this;
    }
}
