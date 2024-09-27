package pinacolada.effects.special;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUI;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.utilities.EUIColors;
import pinacolada.augments.PCLAugment;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;

public class ObtainAugmentEffect extends PCLEffectWithCallback<PCLAugment> {
    private static final float DRAW_SCALE = Settings.scale * 0.5f;
    private static final float Y_RATE = Settings.scale * 300;
    private final PCLAugment augment;
    private float x;
    private float y;
    private float endY;

    public ObtainAugmentEffect(PCLAugment augment, float x, float y) {
        this.augment = augment;
        this.duration = this.startingDuration = 1.5f;
        this.color = EUIColors.white(0f);
        this.x = x;
        this.y = y;
        this.endY = y + Y_RATE;
        this.renderBehind = false;
    }

    public void dispose() {

    }

    protected void firstUpdate(float deltaTime) {
        PGR.dungeon.addAugment(augment.save);
    }

    public void render(SpriteBatch sb) {
        Texture base = augment.getTextureBase();
        Texture tex = augment.getTexture();
        EUI.addPriorityPostRender(s -> {
            EUIRenderHelpers.drawCentered(sb, color, base, x, y, base.getWidth(), base.getHeight(), DRAW_SCALE, DRAW_SCALE);
            if (tex != null) {
                EUIRenderHelpers.drawCentered(sb, color, tex, x, y, tex.getWidth(), tex.getHeight(), DRAW_SCALE, DRAW_SCALE);
            }
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, augment.getName(), x + base.getWidth() * 2f, y, color);
        });
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (this.duration > this.startingDuration / 2f) {
            this.y = EUIRenderHelpers.lerp(this.y, endY, 9 * deltaTime);
            this.color.a = Interpolation.pow2In.apply(1f, 0f, (this.duration - 0.25f) * 4f);
        }
        else {
            this.color.a = Interpolation.pow2Out.apply(0f, 1f, this.duration * 4f);
        }
        super.updateInternal(deltaTime);
    }
}
