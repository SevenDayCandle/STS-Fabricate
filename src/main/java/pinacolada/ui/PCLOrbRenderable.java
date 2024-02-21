package pinacolada.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.orbs.PCLDynamicOrbData;
import pinacolada.orbs.PCLOrbData;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLOrbRenderable extends PCLGenericItemRenderable<PCLOrbData> {
    public static final float BASE_SCALE = Settings.scale * 0.35f;
    protected Texture texture;
    public PCLOrbRenderable(PCLOrbData power) {
        super(power, power.tooltip, new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
    }

    public PCLOrbRenderable(PCLOrbData power, EUIKeywordTooltip tip) {
        super(power, tip, new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
    }

    public PCLOrbRenderable(PCLOrbData power, EUIKeywordTooltip tip, Hitbox hb) {
        super(power, tip, hb);
    }

    public void initializeImage() {
        if (item instanceof PCLDynamicOrbData && ((PCLDynamicOrbData) item).portraitImage != null) {
            this.texture = ((PCLDynamicOrbData) item).portraitImage;
        }
        else {
            this.texture = EUIRM.getTexture(this.item.imagePath);
        }
        if (this.texture == null) {
            this.texture = PCLCoreImages.CardAffinity.unknown.texture();
        }
    }

    public void render(SpriteBatch sb) {
        PCLRenderHelpers.drawCentered(sb, Color.WHITE, texture, this.hb.cX, this.hb.cY, texture.getWidth(), texture.getHeight(), this.scale, this.rotation);
        super.render(sb);
    }
}
