package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIInputManager;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.ui.PCLGenericItemGrid;
import pinacolada.ui.PCLGenericItemRenderable;
import pinacolada.utilities.GameUtilities;

import java.util.Collection;
import java.util.List;

public class PCLGenericSelectRenderableEffect<T extends PCLGenericItemRenderable<?>> extends PCLEffectWithCallback<T> {
    private final Color screenColor;
    private PCLGenericItemGrid<T> grid;
    private boolean draggingScreen;
    private boolean showTopPanelOnComplete;
    private final float targetScale;
    private final float hoveredScale;

    public PCLGenericSelectRenderableEffect(Collection<? extends T> blights, float targetScale, float hoveredScale) {
        super(0.7f);

        this.isRealtime = true;
        this.screenColor = Color.BLACK.cpy();
        this.screenColor.a = 0.8f;
        this.targetScale = targetScale;
        this.hoveredScale = hoveredScale;

        if (GameUtilities.inGame()) {
            AbstractDungeon.overlayMenu.proceedButton.hide();
        }

        this.grid = new PCLGenericItemGrid<T>(targetScale, hoveredScale);
        this.grid.canDragScreen(false);

        if (blights.isEmpty()) {
            complete();
            return;
        }

        if (GameUtilities.isTopPanelVisible()) {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid.add(blights)
                .setOnClick(this::complete);
    }

    @Override
    protected void complete() {
        super.complete();

        if (showTopPanelOnComplete) {
            GameUtilities.setTopPanelVisible(true);
            showTopPanelOnComplete = false;
        }
    }

    public void refresh(List<? extends T> cards) {
        this.grid = new PCLGenericItemGrid<T>(targetScale, hoveredScale);
        this.grid.canDragScreen(false);
        this.grid.add(cards);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        grid.tryRender(sb);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        grid.tryUpdate();

        if (grid.isHovered() || grid.scrollBar.isDragging) {
            return;
        }

        if (EUIInputManager.leftClick.isJustReleased() || EUIInputManager.rightClick.isJustReleased()) {
            complete();
        }
    }
}