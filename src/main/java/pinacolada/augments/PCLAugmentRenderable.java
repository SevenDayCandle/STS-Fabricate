package pinacolada.augments;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.List;

public class PCLAugmentRenderable implements KeywordProvider {
    public final PCLAugment augment;
    public Hitbox hb;
    public ArrayList<EUIKeywordTooltip> tips;
    public EUIKeywordTooltip mainTooltip;
    public float currentX;
    public float currentY;
    public float rotation;
    public float scale;
    public float targetX;
    public float targetY;

    public PCLAugmentRenderable(PCLAugmentData data) {
        this(data.create());
    }

    public PCLAugmentRenderable(PCLAugment augment) {
        this.augment = augment;
        this.hb = new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X);
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

    public void update() {
        this.hb.update();
    }

    public void render(SpriteBatch sb) {
        PCLRenderHelpers.drawColorized(sb, augment.getColor(),
                s -> s.draw(augment.getTexture(), this.currentX - 64.0F, this.currentY - 48.0F, 48.0F, 64.0F, 96f, 96f, this.scale, this.scale, this.rotation, 0, 0, 128, 128, false, false));
        if (this.hb.hovered) {
            this.renderTip(sb);
        }
        this.hb.render(sb);
    }

    public void initializeTips() {
        tips.clear();
        mainTooltip = augment.getTip();
        tips.add(mainTooltip);
        EUITooltip.scanForTips(mainTooltip.description, tips);
    }

    public void renderTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(getTips());
    }
}
