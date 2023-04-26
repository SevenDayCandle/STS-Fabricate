package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIInputManager;
import extendedui.ui.controls.EUICardGrid;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PCLGenericSelectCardEffect extends PCLEffectWithCallback<AbstractCard> {
    private final Color screenColor;
    private CardGroup cards;
    private EUICardGrid grid;
    private boolean draggingScreen;
    private boolean showTopPanelOnComplete;

    public PCLGenericSelectCardEffect(List<? extends AbstractCard> cards) {
        this(GameUtilities.createCardGroup(cards));
    }

    public PCLGenericSelectCardEffect(CardGroup cards) {
        super(0.7f);

        this.cards = cards;
        this.isRealtime = true;
        this.screenColor = Color.BLACK.cpy();
        this.screenColor.a = 0.8f;

        if (GameUtilities.inGame()) {
            AbstractDungeon.overlayMenu.proceedButton.hide();
        }

        if (cards.isEmpty()) {
            this.grid = new EUICardGrid().canDragScreen(false);
            complete();
            return;
        }

        if (GameUtilities.isTopPanelVisible()) {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid = new EUICardGrid()
                .canDragScreen(false)
                .addCards(cards.group)
                .setOnCardClick(this::complete);
    }

    public void refresh(CardGroup cards) {
        this.cards = cards;
        this.grid = new EUICardGrid()
                .canDragScreen(false)
                .addCards(cards.group);
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

    @Override
    protected void complete() {
        super.complete();

        if (showTopPanelOnComplete) {
            GameUtilities.setTopPanelVisible(true);
            showTopPanelOnComplete = false;
        }
    }

    public PCLGenericSelectCardEffect setStartingPosition(float x, float y) {
        for (AbstractCard c : cards.group) {
            c.current_x = x - (c.hb.width * 0.5f);
            c.current_y = y - (c.hb.height * 0.5f);
        }

        return this;
    }
}