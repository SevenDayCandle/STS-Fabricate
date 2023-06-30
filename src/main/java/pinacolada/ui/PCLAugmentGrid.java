package pinacolada.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIItemGrid;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentRenderable;

public class PCLAugmentGrid extends EUIItemGrid<PCLAugmentRenderable> {

    public PCLAugmentGrid() {
        this(0.5f, true);
        targetScale = 0.75f;
        startingScale = targetScale;
        hoveredScale = 1f;
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
    protected float getScrollDistance(PCLAugmentRenderable augment, int index) {
        float scrollDistance = 1f / getRowCount();
        if (augment.targetY > drawTopY) {
            return -scrollDistance;
        }
        else if (augment.targetY < 0) {
            return scrollDistance;
        }
        return 0;
    }

    @Override
    public void updateItemPosition(PCLAugmentRenderable augment, float x, float y) {
        augment.targetX = x;
        augment.targetY = y;
        augment.currentX = EUIUtils.lerpSnap(augment.currentX, augment.targetX, LERP_SPEED);
        augment.currentY = EUIUtils.lerpSnap(augment.currentY, augment.targetY, LERP_SPEED);
    }

    @Override
    public Hitbox getHitbox(PCLAugmentRenderable item) {
        return item.hb;
    }

    @Override
    public void forceUpdateItemPosition(PCLAugmentRenderable augment, float x, float y) {
        augment.currentX = augment.targetX = x;
        augment.currentY = augment.targetY = y;
        augment.hb.update();
        augment.hb.move(augment.currentX, augment.currentY);
    }

    @Override
    protected void updateHoverLogic(PCLAugmentRenderable augment, int i) {
        augment.hb.update();
        augment.hb.move(augment.currentX, augment.currentY);

        if (augment.hb.hovered) {

            hovered = augment;
            hoveredIndex = i;
            augment.scale = MathHelper.scaleLerpSnap(augment.scale, scale(hoveredScale));
        }
        else {
            augment.scale = MathHelper.scaleLerpSnap(augment.scale, scale(targetScale));
        }
    }

    @Override
    protected void renderItem(SpriteBatch sb, PCLAugmentRenderable augment) {
        augment.render(sb);
    }
}
