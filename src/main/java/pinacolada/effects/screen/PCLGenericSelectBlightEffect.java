package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIInputManager;
import extendedui.ui.controls.EUIBlightGrid;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.utilities.GameUtilities;

import java.util.Collection;
import java.util.List;

public class PCLGenericSelectBlightEffect extends PCLEffectWithCallback<AbstractBlight> {
    private final Color screenColor;
    private EUIBlightGrid grid;
    private boolean draggingScreen;
    private boolean showTopPanelOnComplete;

    public PCLGenericSelectBlightEffect(Collection<? extends AbstractBlight> blights) {
        super(0.7f);

        this.isRealtime = true;
        this.screenColor = Color.BLACK.cpy();
        this.screenColor.a = 0.8f;

        if (GameUtilities.inGame()) {
            AbstractDungeon.overlayMenu.proceedButton.hide();
        }

        if (blights.isEmpty()) {
            this.grid = (EUIBlightGrid) new EUIBlightGrid().canDragScreen(false);
            complete();
            return;
        }

        if (GameUtilities.isTopPanelVisible()) {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid = (EUIBlightGrid) new EUIBlightGrid()
                .canDragScreen(false)
                .add(blights)
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

    public void refresh(List<? extends AbstractBlight> cards) {
        this.grid = (EUIBlightGrid) new EUIBlightGrid()
                .canDragScreen(false)
                .add(cards);
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

        if (EUIInputManager.leftClick.isJustPressed() || EUIInputManager.rightClick.isJustPressed()) {
            complete();
        }
    }
}