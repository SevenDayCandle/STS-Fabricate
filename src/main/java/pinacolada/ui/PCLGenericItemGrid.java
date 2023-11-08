package pinacolada.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import extendedui.EUIRenderHelpers;
import extendedui.ui.controls.EUIItemGrid;

public class PCLGenericItemGrid<T extends PCLGenericItemRenderable<?>> extends EUIItemGrid<T> {

    public PCLGenericItemGrid(float targetScale, float hoveredScale) {
        this(targetScale, hoveredScale,0.5f, true);
    }

    public PCLGenericItemGrid(float targetScale, float hoveredScale, float horizontalAlignment) {
        this(targetScale, hoveredScale, horizontalAlignment, true);
    }

    public PCLGenericItemGrid(float targetScale, float hoveredScale, float horizontalAlignment, boolean autoShowScrollbar) {
        super(horizontalAlignment, autoShowScrollbar);
        this.targetScale = this.startingScale = targetScale;
        this.hoveredScale = hoveredScale;
    }

    @Override
    public void forceUpdateItemPosition(T augment, float x, float y) {
        augment.currentX = augment.targetX = x;
        augment.currentY = augment.targetY = y;
        augment.hb.update();
        augment.hb.move(augment.currentX, augment.currentY);
    }

    @Override
    public Hitbox getHitbox(T item) {
        return item.hb;
    }

    @Override
    protected float getScrollDistance(T augment, int index) {
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
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        if (hovered != null) {
            hovered.renderTip(sb);
        }
    }

    @Override
    protected void renderItem(SpriteBatch sb, T augment) {
        augment.render(sb);
    }

    @Override
    protected void updateHoverLogic(T augment, int i) {
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
    public void updateItemPosition(T augment, float x, float y) {
        augment.targetX = x;
        augment.targetY = y;
        augment.currentX = EUIRenderHelpers.lerpSnap(augment.currentX, augment.targetX, LERP_SPEED);
        augment.currentY = EUIRenderHelpers.lerpSnap(augment.currentY, augment.targetY, LERP_SPEED);
    }
}
