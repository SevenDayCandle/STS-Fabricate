package pinacolada.ui.cardEditor;

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

public class PCLCustomCardUpgradableEditor extends EUIHoverable {
    protected static final float ICON_SIZE = 36f * Settings.scale;

    protected ActionT2<Integer, Integer> onUpdate;
    protected EUIButton decreaseButton2;
    protected EUIButton decreaseButton;
    protected EUIButton increaseButton2;
    protected EUIButton increaseButton;
    protected EUILabel header;
    protected EUITextBoxNumericalInput displayValue;
    protected EUITextBoxNumericalInput displayValueSecondary;

    public PCLCustomCardUpgradableEditor(EUIHitbox hb, String title, ActionT2<Integer, Integer> onUpdate) {
        super(hb);

        final float w = hb.width;
        final float h = hb.height;

        decreaseButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.4f, h * 0.5f))
                .setOnClick(this::decreasePrimary)
                .setText(null);

        decreaseButton2 = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.4f, -h * 0.35f))
                .setOnClick(this::decreaseSecondary)
                .setText(null);

        increaseButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, w + (ICON_SIZE * 0.4f), h * 0.5f))
                .setOnClick(this::increasePrimary)
                .setText(null);

        increaseButton2 = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, w + (ICON_SIZE * 0.4f), -h * 0.35f))
                .setOnClick(this::increaseSecondary)
                .setText(null);

        displayValue = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), hb)
                .setOnComplete(this::setValue)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);

        displayValueSecondary = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(),
                new RelativeHitbox(hb, hb.width, hb.height, w * 0.5f, -h * 0.35f))
                .setOnComplete(this::setSecondaryValue)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);

        this.header = new EUILabel(EUIFontHelper.cardtitlefontSmall,
                new RelativeHitbox(hb, w, h, 0, hb.height * 1.2f))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardtitlefontSmall, 0.8f).setColor(Settings.GOLD_COLOR)
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

    public PCLCustomCardUpgradableEditor setHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return setHeader(font, fontScale, textColor, text, false);
    }

    public PCLCustomCardUpgradableEditor setHeader(BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);

        return this;
    }

    public PCLCustomCardUpgradableEditor setHeader(float x, float y, BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setPosition(x, y)
                .setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);

        return this;
    }

    public PCLCustomCardUpgradableEditor setLimits(int minimum, int maximum) {
        displayValue.setLimits(minimum, maximum);

        return this;
    }

    public PCLCustomCardUpgradableEditor setSecondaryValue(int valueSecondary) {
        return setValue(displayValue.getCachedValue(), valueSecondary);
    }

    public PCLCustomCardUpgradableEditor setTooltip(String name, String desc) {
        super.setTooltip(name, desc);
        header.setTooltip(this.tooltip);
        return this;
    }

    public PCLCustomCardUpgradableEditor setTooltip(EUITooltip tip) {
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

    public PCLCustomCardUpgradableEditor setValue(int value) {
        return setValue(value, displayValueSecondary.getCachedValue());
    }

    public PCLCustomCardUpgradableEditor setValue(int value, int valueSecondary) {
        return setValue(value, valueSecondary, true);
    }

    public PCLCustomCardUpgradableEditor setValue(int value, int valueSecondary, boolean invoke) {
        displayValue.forceSetValue(value, false);
        displayValueSecondary.forceSetValue(MathUtils.clamp(valueSecondary, displayValue.getMin() - displayValue.getCachedValue(), displayValue.getMax() - displayValue.getCachedValue()), false);
        if (invoke) {
            onUpdate.invoke(displayValue.getCachedValue(), displayValueSecondary.getCachedValue());
        }

        return this;
    }
}
