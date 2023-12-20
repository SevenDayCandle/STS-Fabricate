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
import pinacolada.dungeon.modifiers.AbstractGlyph;
import pinacolada.resources.PGR;

public class PCLGlyphEditor extends EUIHoverable {
    protected static final float ICON_SIZE = 48f * Settings.scale;
    protected static final int MAX_LEVEL = 99;

    private final AbstractGlyph blight;
    protected EUIButton decreaseButton;
    protected EUIButton increaseButton;
    protected EUIImage image;
    protected int counter;
    protected boolean enabled;

    public PCLGlyphEditor(AbstractGlyph blight, EUIHitbox hb) {
        super(hb);
        this.blight = blight;

        final float w = hb.width;
        final float h = hb.height;

        decreaseButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(hb, ICON_SIZE * 0.9f, ICON_SIZE * 0.9f, (ICON_SIZE * 0.25f), h * -0.25f))
                .setOnClick(this::decrease);

        increaseButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(hb, ICON_SIZE * 0.9f, ICON_SIZE * 0.9f, w - (ICON_SIZE * 0.25f), h * -0.25f))
                .setOnClick(this::increase);

        image = new EUIImage(blight.getImage(), Color.WHITE).setHitbox(hb);

        tooltip = new EUITooltip(blight.getTitle(), blight.getDescription(0));
    }

    public void decrease() {
        if (enabled && counter > 0) {
            counter -= 1;
            blight.configOption.set(counter);
        }
    }

    public void increase() {
        if (enabled && counter < MAX_LEVEL) {
            counter += 1;
            blight.configOption.set(counter);
        }
    }

    public void refresh(int ascensionLevel) {
        enabled = EUIUtils.any(PGR.getRegisteredPlayerResources(), r -> r.getUnlockLevel() >= blight.ascensionRequirement);
        if (counter < 0) {
            counter = 0;
            blight.configOption.set(counter);
        }
        tooltip.setDescription(enabled ? blight.getDescription(ascensionLevel) : blight.getLockedTooltipDescription());
        image.setShaderMode(enabled ? EUIRenderHelpers.ShaderMode.Normal : EUIRenderHelpers.ShaderMode.Grayscale);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        image.renderImpl(sb);
        FontHelper.renderFontRightTopAligned(sb, FontHelper.cardTitleFont, String.valueOf(counter), image.hb.cX + image.hb.width / 4, image.hb.y, 1f, EUIColors.white(1f));
        decreaseButton.tryRender(sb);
        increaseButton.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        this.hb.update();
        image.updateImpl();
        decreaseButton.setInteractable(enabled && counter > 0).updateImpl();
        increaseButton.setInteractable(enabled && counter < MAX_LEVEL).updateImpl();
    }
}
