package pinacolada.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLDynamicAugmentData;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLAugmentRenderable extends PCLGenericItemRenderable<PCLAugment> {
    public static final float BASE_SCALE = Settings.scale * 0.35f;
    protected Texture texture;
    protected Texture textureBase;

    public PCLAugmentRenderable(PCLAugment power) {
        super(power, power.getTooltip(), new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
        scale = BASE_SCALE;
    }

    public PCLAugmentRenderable(PCLAugment power, EUIKeywordTooltip tip) {
        super(power, tip, new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
        scale = BASE_SCALE;
    }

    public PCLAugmentRenderable(PCLAugment power, EUIKeywordTooltip tip, Hitbox hb) {
        super(power, tip, hb);
        scale = BASE_SCALE;
    }

    @Override
    public void initializeImage() {
        textureBase = item.getTextureBase();
        if (this.item.data instanceof PCLDynamicAugmentData && ((PCLDynamicAugmentData) this.item.data).portraitImage != null) {
            this.texture = ((PCLDynamicAugmentData) this.item.data).portraitImage;
        }
        else {
            texture = item.getTexture();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        PCLRenderHelpers.drawCentered(sb, Color.WHITE, textureBase, this.hb.cX, this.hb.cY, 128f, 128f, this.scale, this.rotation);
        if (texture != null) {
            int h = texture.getHeight();
            PCLRenderHelpers.drawCentered(sb, Color.WHITE, texture, this.hb.cX, this.hb.cY, h, h, this.scale, this.rotation);
        }
        super.render(sb);
    }
}
