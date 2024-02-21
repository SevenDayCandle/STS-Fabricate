package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.ui.controls.EUICardGrid;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.utilities.GameUtilities;

import java.util.Collection;
import java.util.List;

public class PCLGenericSelectCardEffect extends PCLEffectWithCallback<AbstractCard> {
    private final Color screenColor;
    private EUICardGrid grid;
    private boolean draggingScreen;
    private boolean showTopPanelOnComplete;

    public PCLGenericSelectCardEffect(Collection<? extends AbstractCard> cards) {
        super(0.7f);

        this.isRealtime = true;
        this.screenColor = Color.BLACK.cpy();
        this.screenColor.a = 0.8f;

        if (GameUtilities.inGame()) {
            AbstractDungeon.overlayMenu.proceedButton.hide();
        }

        if (cards.isEmpty()) {
            this.grid = (EUICardGrid) new EUICardGrid().canDragScreen(false);
            complete();
            return;
        }

        if (GameUtilities.isTopPanelVisible()) {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid = (EUICardGrid) new EUICardGrid()
                .setItems(cards)
                .canDragScreen(false)
                .setOnClick(this::complete);

        EUI.cardFilters.initializeForSort(grid.group, __ -> {
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, EUI.actingColor);
    }

    @Override
    protected void complete() {
        super.complete();

        if (showTopPanelOnComplete) {
            GameUtilities.setTopPanelVisible(true);
            showTopPanelOnComplete = false;
        }
    }

    public void refresh(List<? extends AbstractCard> cards) {
        this.grid = (EUICardGrid) new EUICardGrid()
                .setItems(cards)
                .canDragScreen(false);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        grid.tryRender(sb);
        EUI.sortHeader.render(sb);
        if (!EUI.cardFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
        }
    }

    @Override
    protected void updateInternal(float deltaTime) {
        boolean shouldDoStandardUpdate = !EUI.cardFilters.tryUpdate();
        if (shouldDoStandardUpdate) {
            grid.tryUpdate();
            EUI.sortHeader.update();
            EUI.openFiltersButton.update();

            if (grid.isHovered() || EUI.sortHeader.isHovered() || EUI.openFiltersButton.hb.hovered || grid.scrollBar.isDragging) {
                return;
            }

            if (EUIInputManager.leftClick.isJustPressed() || EUIInputManager.rightClick.isJustPressed()) {
                complete();
            }
        }

    }
}