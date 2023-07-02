package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUITextBoxInput;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;

public class PCLCustomColorEditor extends EUIHoverable {
    protected static final float ICON_SIZE = scale(36f);
    protected ActionT1<PCLCustomColorEditor> onOpen;
    protected ActionT1<Color> onUpdate;
    protected Color current = Color.WHITE.cpy();
    protected EUIButton colorButton;
    protected EUITextBoxInput hexInput;
    public EUILabel header;

    public PCLCustomColorEditor(EUIHitbox hb, String title, ActionT1<PCLCustomColorEditor> onOpen, ActionT1<Color> onUpdate) {
        super(hb);

        final float w = hb.width;
        final float h = hb.height;

        this.onOpen = onOpen;
        this.onUpdate = onUpdate;

        colorButton = new EUIButton(EUIRM.images.squaredButton2.texture(), new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, hb.width + ICON_SIZE, h * 0.5f))
                .setOnClick(this::open);
        colorButton.background.setShaderMode(EUIRenderHelpers.ShaderMode.Colorize);

        hexInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.panelRoundedHalfH.texture(), hb)
                .setOnComplete(this::setColorFromHex)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

        this.header = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                RelativeHitbox.fromPercentages(hb, 1, 1, 0.5f, 1.5f))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.8f).setColor(Settings.GOLD_COLOR)
                .setLabel(title);

    }

    public Color getColor() {
        return current;
    }

    public EUITourTooltip makeTour(boolean canDismiss) {
        if (this.tooltip != null) {
            EUITourTooltip tip = new EUITourTooltip(this.hb, this.tooltip.title, this.tooltip.description);
            tip.setFlash(this.hexInput.image);
            tip.setCanDismiss(canDismiss);
            return tip;
        }
        else {
            return null;
        }
    }

    public void open() {
        onOpen.invoke(this);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        this.hb.render(sb);
        colorButton.tryRender(sb);
        hexInput.tryRender(sb);
        header.tryRender(sb);
    }

    public PCLCustomColorEditor setColor(Color value) {
        return setColor(value, true);
    }

    public PCLCustomColorEditor setColor(Color value, boolean invoke) {
        if (value != null) {
            current = value.cpy();
            colorButton.setColor(current);
            hexInput.setLabel(current.toString());
            if (invoke) {
                onUpdate.invoke(current);
            }
        }

        return this;
    }

    public void setColorFromHex(String input) {
        try {
            current = Color.valueOf(input);
            onUpdate.invoke(current);
        }
        catch (Exception ignored) {
        }
    }

    public PCLCustomColorEditor setHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return setHeader(font, fontScale, textColor, text, false);
    }

    public PCLCustomColorEditor setHeader(BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);

        return this;
    }

    public PCLCustomColorEditor setHeader(float x, float y, BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setPosition(x, y)
                .setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);

        return this;
    }

    public PCLCustomColorEditor setTooltip(String name, String desc) {
        super.setTooltip(name, desc);
        header.setTooltip(this.tooltip);
        return this;
    }

    public PCLCustomColorEditor setTooltip(EUITooltip tip) {
        super.setTooltip(tip);
        header.setTooltip(tip);
        return this;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        colorButton.tryUpdate();
        hexInput.tryUpdate();
        header.tryUpdate();
    }
}
