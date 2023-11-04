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
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.pcl.PCLCoreImages;

public class PCLPowerRenderable extends PCLGridRenderable<PCLPowerData> {
    public static final float BASE_SCALE = Settings.scale * 0.6f;
    protected Texture texture;
    protected TextureRegion region;
    public PCLPowerRenderable(PCLPowerData power) {
        super(power, power.tooltip, new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
    }

    public PCLPowerRenderable(PCLPowerData power, EUIKeywordTooltip tip) {
        super(power, tip, new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
    }

    public PCLPowerRenderable(PCLPowerData power, EUIKeywordTooltip tip, Hitbox hb) {
        super(power, tip, hb);
    }

    public void initializeImage() {
        if (this.item.useRegionImage) {
            this.region = AbstractPower.atlas.findRegion("128/" + this.item.imagePath);
        }
        else {
            this.texture = EUIRM.getTexture(this.item.imagePath);
        }
        if (this.texture == null && this.region == null) {
            this.texture = PCLCoreImages.CardAffinity.unknown.texture();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        if (this.region != null) {
            sb.draw(region, this.hb.x, this.hb.y, 42.0F, 42.0F, 84f, 84f, this.scale, this.scale, this.rotation);
        }
        else {
            sb.draw(texture, this.hb.x, this.hb.y, 42.0F, 42.0F, 84f, 84f, this.scale, this.scale, this.rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        }
        if (amountText != null) {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, amountText, this.hb.x + this.hb.width, this.hb.y + 15f * Settings.scale, 1.5f, Settings.GREEN_TEXT_COLOR);
        }

        super.render(sb);
    }
}
