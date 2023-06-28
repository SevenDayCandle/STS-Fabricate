package pinacolada.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIItemGrid;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentRenderable;

public class PCLAugmentGrid extends EUIItemGrid<PCLAugmentRenderable> {

    public PCLAugmentGrid() {
        this(0.5f, true);
    }

    public PCLAugmentGrid(float horizontalAlignment, boolean autoShowScrollbar) {
        super(horizontalAlignment, autoShowScrollbar);
    }

    public PCLAugmentGrid(float horizontalAlignment) {
        this(horizontalAlignment, true);
    }
    
    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        if (hovered != null) {
            hovered.renderTip(sb);
        }
    }

    @Override
    protected float getScrollDistance(PCLAugmentRenderable blight, int index) {
        float scrollDistance = 1f / getRowCount();
        if (blight.targetY > drawTopY) {
            return -scrollDistance;
        }
        else if (blight.targetY < 0) {
            return scrollDistance;
        }
        return 0;
    }

    @Override
    public void updateItemPosition(PCLAugmentRenderable blight, float x, float y) {
        blight.targetX = x;
        blight.targetY = y;
        blight.currentX = EUIUtils.lerpSnap(blight.currentX, blight.targetX, LERP_SPEED);
        blight.currentY = EUIUtils.lerpSnap(blight.currentY, blight.targetY, LERP_SPEED);
    }

    @Override
    public Hitbox getHitbox(PCLAugmentRenderable item) {
        return item.hb;
    }

    @Override
    public void forceUpdateItemPosition(PCLAugmentRenderable blight, float x, float y) {
        blight.currentX = blight.targetX = x;
        blight.currentY = blight.targetY = y;
        blight.hb.update();
        blight.hb.move(blight.currentX, blight.currentY);
    }

    @Override
    protected void updateHoverLogic(PCLAugmentRenderable blight, int i) {
        blight.hb.update();
        blight.hb.move(blight.currentX, blight.currentY);

        if (blight.hb.hovered) {

            hovered = blight;
            hoveredIndex = i;
            blight.scale = MathHelper.scaleLerpSnap(blight.scale, scale(hoveredScale));
        }
        else {
            blight.scale = MathHelper.scaleLerpSnap(blight.scale, scale(targetScale));
        }
    }

    @Override
    protected void renderItem(SpriteBatch sb, PCLAugmentRenderable blight) {
        blight.render(sb);
    }
}
