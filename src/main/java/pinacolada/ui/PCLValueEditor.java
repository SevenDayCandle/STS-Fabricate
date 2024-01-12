package pinacolada.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUITextBoxNumericalInput;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;

public class PCLValueEditor extends EUIHoverable {
    public static final float ICON_SIZE = scale(36f);
    protected ActionT1<Integer> onUpdate;
    protected EUIButton decreaseButton;
    protected EUIButton increaseButton;
    protected EUITextBoxNumericalInput displayValue;
    protected boolean allowZero = true;
    public EUILabel header;

    public PCLValueEditor(EUIHitbox hb, String title, ActionT1<Integer> onUpdate) {
        this(hb, title, onUpdate, ICON_SIZE);
    }

    public PCLValueEditor(EUIHitbox hb, String title, ActionT1<Integer> onUpdate, float iconSize) {
        super(hb);

        final float w = hb.width;
        final float h = hb.height;

        decreaseButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(hb, iconSize, iconSize, iconSize * -0.4f, h * 0.5f))
                .setOnClick(this::decrease);

        increaseButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(hb, iconSize, iconSize, w + (iconSize * 0.4f), h * 0.5f))
                .setOnClick(this::increase);

        displayValue = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), hb)
                .setOnComplete(this::setValue)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(FontHelper.topPanelAmountFont, 1f);

        this.header = new EUILabel(FontHelper.topPanelAmountFont,
                RelativeHitbox.fromPercentages(hb, 1, 1, 0f, 1.5f))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(FontHelper.topPanelAmountFont, 0.8f).setColor(Settings.GOLD_COLOR)
                .setLabel(title);

        this.onUpdate = onUpdate;
    }

    public void decrease() {
        setValue(displayValue.getCachedValue() - 1);
    }

    public int getValue() {
        return displayValue.getCachedValue();
    }

    public void increase() {
        setValue(displayValue.getCachedValue() + 1);
    }

    public boolean isHovered() {
        return hb.hovered || decreaseButton.hb.hovered || increaseButton.hb.hovered;
    }

    public EUITourTooltip makeTour(boolean canDismiss) {
        if (this.tooltip != null && isActive) {
            EUITourTooltip tip = new EUITourTooltip(this.hb, this.tooltip.title, this.tooltip.description);
            tip.setFlash(this.displayValue.image);
            tip.setCanDismiss(canDismiss);
            return tip;
        }
        else {
            return null;
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        this.hb.render(sb);
        decreaseButton.tryRender(sb);
        increaseButton.tryRender(sb);
        displayValue.tryRender(sb);
        header.tryRender(sb);
    }

    public PCLValueEditor setHasInfinite(boolean isInfinite, boolean allowZero) {
        this.displayValue.showNegativeAsInfinity(isInfinite);
        this.allowZero = allowZero;
        return this;
    }

    public PCLValueEditor setHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return setHeader(font, fontScale, textColor, text, false);
    }

    public PCLValueEditor setHeader(BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);

        return this;
    }

    public PCLValueEditor setHeader(float x, float y, BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setPosition(x, y)
                .setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);

        return this;
    }

    public PCLValueEditor setLimits(int minimum, int maximum) {
        displayValue.setLimits(minimum, maximum);

        return this;
    }

    public PCLValueEditor setTooltip(String name, String desc) {
        super.setTooltip(name, desc);
        header.setTooltip(this.tooltip);
        return this;
    }

    public PCLValueEditor setTooltip(EUITooltip tip) {
        super.setTooltip(tip);
        header.setTooltip(tip);
        return this;
    }

    public PCLValueEditor setValue(int value) {
        return setValue(value, true);
    }

    public PCLValueEditor setValue(int value, boolean invoke) {
        displayValue.forceSetValue(displayValue.showNegativeAsInfinity && (value < 0 || (value == 0 && !allowZero)) ? -1 : value, false);
        if (invoke) {
            onUpdate.invoke(displayValue.getCachedValue());
        }

        return this;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        decreaseButton.setInteractable(displayValue.getCachedValue() > displayValue.getMin()).tryUpdate();
        increaseButton.setInteractable(displayValue.getCachedValue() < displayValue.getMax()).tryUpdate();
        displayValue.tryUpdate();
        header.tryUpdate();
    }
}
