package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIColors;
import pinacolada.blights.common.AbstractGlyphBlight;
import pinacolada.resources.PGR;

public class PCLGlyphEditor extends EUIHoverable {
    protected static final float ICON_SIZE = 48f * Settings.scale;

    private final AbstractGlyphBlight blight;
    protected EUIButton decreaseButton;
    protected EUIButton increaseButton;
    protected EUIImage image;
    protected boolean enabled;
    protected int minimumLevel = 0;
    protected int maximumLevel = 99;

    public PCLGlyphEditor(AbstractGlyphBlight blight, EUIHitbox hb) {
        super(hb);
        this.blight = blight;

        final float w = hb.width;
        final float h = hb.height;

        decreaseButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(hb, ICON_SIZE * 0.9f, ICON_SIZE * 0.9f, (ICON_SIZE * 0.25f), h * -0.25f))
                .setOnClick(this::decrease)
                .setText(null);

        increaseButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(hb, ICON_SIZE * 0.9f, ICON_SIZE * 0.9f, w - (ICON_SIZE * 0.25f), h * -0.25f))
                .setOnClick(this::increase)
                .setText(null);

        image = new EUIImage(blight.img, Color.WHITE).setHitbox(hb);

        tooltip = new EUITooltip(blight.strings.NAME, blight.getAscensionTooltipDescription(0));
    }

    public void decrease() {
        if (enabled && blight.counter > minimumLevel) {
            blight.addAmount(-1);
            blight.configOption.set(blight.counter, true);
        }
    }

    public void increase() {
        if (enabled && blight.counter < maximumLevel) {
            blight.addAmount(1);
            blight.configOption.set(blight.counter, true);
        }
    }

    public void refresh(int ascensionLevel) {
        enabled = EUIUtils.any(PGR.getRegisteredResources(), r -> r.getUnlockLevel() >= blight.ascensionRequirement);
        minimumLevel = blight.getMinimumLevel(ascensionLevel);
        if (blight.counter < minimumLevel) {
            blight.setAmount(minimumLevel);
            blight.configOption.set(blight.counter, true);
        }
        tooltip.setDescription(enabled ? blight.getAscensionTooltipDescription(ascensionLevel) : blight.getLockedTooltipDescription());
        image.setShaderMode(enabled ? EUIRenderHelpers.ShaderMode.Normal : EUIRenderHelpers.ShaderMode.Grayscale);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        image.renderImpl(sb);
        FontHelper.renderFontRightTopAligned(sb, FontHelper.cardTitleFont, String.valueOf(blight.counter), image.hb.cX + image.hb.width / 4, image.hb.y, 1f, EUIColors.white(1f));
        decreaseButton.tryRender(sb);
        increaseButton.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        this.hb.update();
        image.updateImpl();
        decreaseButton.setInteractable(enabled && blight.counter > minimumLevel).updateImpl();
        increaseButton.setInteractable(enabled && blight.counter < maximumLevel).updateImpl();
    }
}
