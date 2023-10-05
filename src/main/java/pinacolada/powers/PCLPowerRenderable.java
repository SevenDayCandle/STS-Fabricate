package pinacolada.powers;

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
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.pcl.PCLCoreImages;

import java.util.ArrayList;
import java.util.List;

public class PCLPowerRenderable implements KeywordProvider {
    public static final float BASE_SCALE = Settings.scale * 0.6f;
    public final PCLPowerData power;
    protected Texture texture;
    protected TextureRegion region;
    public Hitbox hb;
    public ArrayList<EUIKeywordTooltip> tips;
    public EUIKeywordTooltip mainTooltip;
    public String amountText;
    public float currentX;
    public float currentY;
    public float rotation;
    public float scale = BASE_SCALE;
    public float targetX;
    public float targetY;

    public PCLPowerRenderable(PCLPowerData power) {
        this(power, power.tooltip, new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
    }

    public PCLPowerRenderable(PCLPowerData power, EUIKeywordTooltip tip) {
        this(power, tip, new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
    }

    public PCLPowerRenderable(PCLPowerData power, EUIKeywordTooltip tip, Hitbox hb) {
        this.power = power;
        this.hb = hb;
        tips = new ArrayList<>();
        initializeTips(tip);
        initializeImage();
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tips;
    }

    @Override
    public List<EUIKeywordTooltip> getTipsForFilters() {
        return tips.subList(1, tips.size());
    }

    public void initializeImage() {
        if (this.power.useRegionImage) {
            this.region = AbstractPower.atlas.findRegion("128/" + this.power.imagePath);
        }
        else {
            this.texture = EUIRM.getTexture(this.power.imagePath);
        }
        if (this.texture == null && this.region == null) {
            this.texture = PCLCoreImages.CardAffinity.unknown.texture();
        }
    }

    public void initializeTips(EUIKeywordTooltip tip) {
        tips.clear();
        mainTooltip = tip;
        if (mainTooltip != null) {
            tips.add(mainTooltip);
            // Prevent "duplicates"
            if (power.tooltip != null && mainTooltip != power.tooltip) {
                tips.add(power.tooltip);
                EUITooltip.scanForTips(mainTooltip.description, tips);
                tips.remove(power.tooltip);
            }
            else {
                EUITooltip.scanForTips(mainTooltip.description, tips);
            }

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

        if (this.hb.hovered) {
            this.renderTip(sb);
        }
        this.hb.render(sb);
    }

    public void renderTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(getTips());
    }

    public void update() {
        this.hb.update();
    }
}
