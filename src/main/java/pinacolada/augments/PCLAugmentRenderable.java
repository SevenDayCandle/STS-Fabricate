package pinacolada.augments;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public class PCLAugmentRenderable implements KeywordProvider {
    public static final float BASE_SCALE = Settings.scale * 0.6f;

    public final PCLAugment augment;
    public Hitbox hb;
    public ArrayList<EUIKeywordTooltip> tips;
    public EUIKeywordTooltip mainTooltip;
    public float currentX;
    public float currentY;
    public float rotation;
    public float scale = BASE_SCALE;
    public float targetX;
    public float targetY;

    public PCLAugmentRenderable(PCLAugmentData data) {
        this(data.create());
    }

    public PCLAugmentRenderable(PCLAugment augment) {
        this(augment, new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
    }

    public PCLAugmentRenderable(PCLAugment augment, Hitbox hb) {
        this.augment = augment;
        this.hb = hb;
        tips = new ArrayList<>();
        initializeTips();
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tips;
    }

    @Override
    public List<EUIKeywordTooltip> getTipsForFilters() {
        return tips.subList(1, tips.size());
    }

    public void initializeTips() {
        tips.clear();
        mainTooltip = augment.getTip();
        tips.add(mainTooltip);
        EUITooltip.scanForTips(mainTooltip.description, tips);
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(augment.getTextureBase(), this.hb.x, this.hb.y, 64.0F, 64.0F, 128f, 128f, this.scale, this.scale, this.rotation, 0, 0, 128, 128, false, false);
        sb.draw(augment.getTexture(), this.hb.x, this.hb.y, 64.0F, 64.0F, 128f, 128f, this.scale, this.scale, this.rotation, 0, 0, 128, 128, false, false);
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
