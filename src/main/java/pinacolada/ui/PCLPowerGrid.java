package pinacolada.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import extendedui.EUIRenderHelpers;
import extendedui.ui.controls.EUIItemGrid;

public class PCLPowerGrid extends EUIItemGrid<PCLPowerRenderable> {

    public PCLPowerGrid() {
        this(0.5f, true);
        targetScale = PCLPowerRenderable.BASE_SCALE;
        startingScale = targetScale;
        hoveredScale = 0.9f;
    }

    public PCLPowerGrid(float horizontalAlignment, boolean autoShowScrollbar) {
        super(horizontalAlignment, autoShowScrollbar);
    }

    public PCLPowerGrid(float horizontalAlignment) {
        this(horizontalAlignment, true);
    }

    @Override
    public void forceUpdateItemPosition(PCLPowerRenderable augment, float x, float y) {
        augment.currentX = augment.targetX = x;
        augment.currentY = augment.targetY = y;
        augment.hb.update();
        augment.hb.move(augment.currentX, augment.currentY);
    }

    @Override
    public Hitbox getHitbox(PCLPowerRenderable item) {
        return item.hb;
    }

    @Override
    protected float getScrollDistance(PCLPowerRenderable augment, int index) {
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
    protected void renderItem(SpriteBatch sb, PCLPowerRenderable augment) {
        augment.render(sb);
    }

    @Override
    protected void updateHoverLogic(PCLPowerRenderable augment, int i) {
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
    public void updateItemPosition(PCLPowerRenderable augment, float x, float y) {
        augment.targetX = x;
        augment.targetY = y;
        augment.currentX = EUIRenderHelpers.lerpSnap(augment.currentX, augment.targetX, LERP_SPEED);
        augment.currentY = EUIRenderHelpers.lerpSnap(augment.currentY, augment.targetY, LERP_SPEED);
    }
}
