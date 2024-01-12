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

public class PCLAugmentRenderable extends PCLGenericItemRenderable<PCLAugment> {
    public static final float BASE_SCALE = Settings.scale * 0.45f;
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
        sb.draw(textureBase, this.hb.x, this.hb.y, 64.0F, 64.0F, 128f, 128f, this.scale, this.scale, this.rotation, 0, 0, 128, 128, false, false);
        if (texture != null) {
            int h = texture.getHeight();
            sb.draw(texture, this.hb.x, this.hb.y, h / 2f, h / 2f, h, h, this.scale, this.scale, this.rotation, 0, 0, h, h, false, false);
        }
        super.render(sb);
    }
}
