package pinacolada.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public abstract class PCLGenericItemRenderable<T extends KeywordProvider> implements KeywordProvider {
    public static final float BASE_SCALE = Settings.scale * 0.6f;
    public final T item;
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

    public PCLGenericItemRenderable(T item) {
        this(item, item.getTooltip(), new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
    }

    public PCLGenericItemRenderable(T item, EUIKeywordTooltip tip) {
        this(item, tip, new Hitbox(AbstractRelic.PAD_X, AbstractRelic.PAD_X));
    }

    public PCLGenericItemRenderable(T item, EUIKeywordTooltip tip, Hitbox hb) {
        this.item = item;
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

    public abstract void initializeImage();

    public void initializeTips(EUIKeywordTooltip tip) {
        tips.clear();
        mainTooltip = tip;
        if (mainTooltip != null) {
            tips.add(mainTooltip);
            // Prevent "duplicates"
            EUIKeywordTooltip existing = item.getTooltip();
            if (existing != null && mainTooltip != existing) {
                tips.add(existing);
                EUITooltip.scanForTips(mainTooltip.description, tips);
                tips.remove(existing);
            }
            else {
                EUITooltip.scanForTips(mainTooltip.description, tips);
            }

        }
    }

    public void render(SpriteBatch sb) {
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
