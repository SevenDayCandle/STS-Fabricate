package pinacolada.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.orbs.PCLOrbData;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.pcl.PCLCoreImages;

public class PCLOrbRenderable extends PCLGenericItemRenderable<PCLOrbData> {
    public static final float BASE_SCALE = Settings.scale * 0.6f;
    protected Texture texture;
    protected TextureRegion region;
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
        this.texture = EUIRM.getTexture(this.item.imagePath);
        if (this.texture == null) {
            this.texture = PCLCoreImages.CardAffinity.unknown.texture();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(texture, this.hb.x, this.hb.y, 42.0F, 42.0F, 84f, 84f, this.scale, this.scale, this.rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        super.render(sb);
    }
}
