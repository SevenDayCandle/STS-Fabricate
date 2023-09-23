package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.utilities.RelicInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PCLGenericSelectRelicEffect extends PCLEffectWithCallback<AbstractRelic> {
    private final Color screenColor;
    private List<? extends AbstractRelic> relics;
    private EUIRelicGrid grid;
    private boolean draggingScreen;
    private boolean showTopPanelOnComplete;

    public PCLGenericSelectRelicEffect(List<? extends AbstractRelic> relics) {
        super(0.7f);

        this.relics = relics;
        this.isRealtime = true;
        this.screenColor = Color.BLACK.cpy();
        this.screenColor.a = 0.8f;

        if (GameUtilities.inGame()) {
            AbstractDungeon.overlayMenu.proceedButton.hide();
        }

        if (relics.isEmpty()) {
            this.grid = (EUIRelicGrid) new EUIRelicGrid().canDragScreen(false);
            complete();
            return;
        }

        if (GameUtilities.isTopPanelVisible()) {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid = (EUIRelicGrid) new EUIRelicGrid()
                .canDragScreen(false)
                .add(relics, RelicInfo::new)
                .setOnClick(r -> complete(r.relic));

        EUI.relicFilters.initializeForSort(grid.group, __ -> {
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

    public void refresh(List<? extends AbstractRelic> cards) {
        this.relics = cards;
        this.grid = (EUIRelicGrid) new EUIRelicGrid()
                .canDragScreen(false)
                .add(cards, RelicInfo::new);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        grid.tryRender(sb);
        EUI.sortHeader.render(sb);
        if (!EUI.relicFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
        }
    }

    public PCLGenericSelectRelicEffect setStartingPosition(float x, float y) {
        for (AbstractRelic c : relics) {
            c.currentX = x - (c.hb.width * 0.5f);
            c.currentY = y - (c.hb.height * 0.5f);
        }

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        boolean shouldDoStandardUpdate = !EUI.relicFilters.tryUpdate();
        if (shouldDoStandardUpdate) {
            grid.tryUpdate();
            EUI.sortHeader.update();
            EUI.openFiltersButton.update();

            if (grid.isHovered() || EUI.sortHeader.isHovered() || EUI.openFiltersButton.hb.hovered || grid.scrollBar.isDragging) {
                return;
            }

            if (EUIInputManager.leftClick.isJustReleased() || EUIInputManager.rightClick.isJustReleased()) {
                complete();
            }
        }
    }
}