package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIInputManager;
import extendedui.ui.controls.EUIPotionGrid;
import extendedui.utilities.PotionInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PCLGenericSelectPotionEffect extends PCLEffectWithCallback<AbstractPotion> {
    private final Color screenColor;
    private List<? extends AbstractPotion> potions;
    private EUIPotionGrid grid;
    private boolean draggingScreen;
    private boolean showTopPanelOnComplete;

    public PCLGenericSelectPotionEffect(List<? extends AbstractPotion> potions) {
        super(0.7f);

        this.potions = potions;
        this.isRealtime = true;
        this.screenColor = Color.BLACK.cpy();
        this.screenColor.a = 0.8f;

        if (GameUtilities.inGame()) {
            AbstractDungeon.overlayMenu.proceedButton.hide();
        }

        if (potions.isEmpty()) {
            this.grid = (EUIPotionGrid) new EUIPotionGrid().canDragScreen(false);
            complete();
            return;
        }

        if (GameUtilities.isTopPanelVisible()) {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid = (EUIPotionGrid) new EUIPotionGrid()
                .canDragScreen(false)
                .add(potions, PotionInfo::new)
                .setOnClick(c -> complete(c.potion));
    }

    @Override
    protected void complete() {
        super.complete();

        if (showTopPanelOnComplete) {
            GameUtilities.setTopPanelVisible(true);
            showTopPanelOnComplete = false;
        }
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

    public void refresh(List<? extends AbstractPotion> cards) {
        this.potions = cards;
        this.grid = (EUIPotionGrid) new EUIPotionGrid()
                .canDragScreen(false)
                .add(cards, PotionInfo::new);
    }

    public PCLGenericSelectPotionEffect setStartingPosition(float x, float y) {
        for (AbstractPotion c : potions) {
            c.posX = x - (c.hb.width * 0.5f);
            c.posY = y - (c.hb.height * 0.5f);
        }

        return this;
    }
}