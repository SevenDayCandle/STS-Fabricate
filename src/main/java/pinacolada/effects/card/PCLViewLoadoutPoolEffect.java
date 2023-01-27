package pinacolada.effects.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUICardGrid;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;
import pinacolada.ui.characterSelection.PCLSeriesSelectScreen;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

import static pinacolada.utilities.GameUtilities.scale;
import static pinacolada.utilities.GameUtilities.screenW;

public class PCLViewLoadoutPoolEffect extends PCLEffectWithCallback<CardGroup>
{
    private static final float DUR = 1.5f;
    private CardGroup cards;
    private EUIButton deselectAllButton;
    private EUIButton selectAllButton;
    private EUICardGrid grid;
    private EUILabel selectedCount;
    private EUIToggle upgradeToggle;
    private boolean draggingScreen;
    private boolean canToggle = true;
    private boolean showTopPanelOnComplete;
    private final Color screenColor;
    private final PCLSeriesSelectScreen screen;

    public PCLViewLoadoutPoolEffect(PCLSeriesSelectScreen screen, ArrayList<AbstractCard> cards)
    {
        this(screen, GameUtilities.createCardGroup(cards));
    }

    public PCLViewLoadoutPoolEffect(PCLSeriesSelectScreen screen, CardGroup cards)
    {
        super(0.7f);

        this.screen = screen;
        this.cards = cards;
        this.isRealtime = true;
        this.screenColor = Color.BLACK.cpy();
        this.screenColor.a = 0.8f;

        if (GameUtilities.inGame())
        {
            AbstractDungeon.overlayMenu.proceedButton.hide();
        }

        if (cards.isEmpty())
        {
            this.grid = new EUICardGrid().canDragScreen(false);
            complete(cards);
            return;
        }

        if (GameUtilities.isTopPanelVisible())
        {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid = new EUICardGrid()
                .canDragScreen(false)
                .addCards(cards.group)
                .setOnCardClick(this::toggleCard);
        for (AbstractCard c : cards.group)
        {
            updateCardAlpha(c);
        }

        final float xPos = screenW(0.075f);
        final float buttonWidth = scale(256);
        final float buttonHeight = scale(48);

        selectedCount = new EUILabel(FontHelper.tipHeaderFont, new EUIHitbox(xPos, Settings.HEIGHT * 0.75f, buttonWidth, buttonHeight * 2f))
                .setPosition(xPos, Settings.HEIGHT * 0.75f)
                .setColor(Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f, true)
                .setFont(FontHelper.tipHeaderFont, 1);

        selectAllButton = AbstractScreen.createHexagonalButton(xPos, Settings.HEIGHT * 0.65f, buttonWidth, buttonHeight)
                .setText(PGR.core.strings.seriesSelectionButtons.selectAll)
                .setPosition(xPos, Settings.HEIGHT * 0.65f)
                .setOnClick(() -> this.toggleCards(true))
                .setColor(Color.ROYAL);

        deselectAllButton =  AbstractScreen.createHexagonalButton(xPos, selectAllButton.hb.y - selectAllButton.hb.height, buttonWidth, buttonHeight)
                .setText(PGR.core.strings.seriesSelectionButtons.deselectAll)
                .setPosition(xPos, selectAllButton.hb.y - selectAllButton.hb.height)
                .setOnClick(() -> this.toggleCards(false))
                .setColor(Color.FIREBRICK);

        upgradeToggle = new EUIToggle(new EUIHitbox(buttonWidth, buttonHeight))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setPosition(xPos, deselectAllButton.hb.y - deselectAllButton.hb.height * 2)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(this::toggleViewUpgrades);

        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
        refreshCountText();
    }

    private void updateCardAlpha(AbstractCard c)
    {
        c.targetTransparency = screen.container.bannedCards.contains(c.cardID) ? 0.35f : 1f;
    }

    private void toggleCard(AbstractCard c)
    {
        if (canToggle)
        {
            toggleCardImpl(c, screen.container.bannedCards.contains(c.cardID));
            refreshCountText();
        }
    }

    private void toggleCardImpl(AbstractCard c, boolean value)
    {
        if (value)
        {
            screen.container.bannedCards.remove(c.cardID);
        }
        else {
            screen.container.bannedCards.add(c.cardID);
        }
        updateCardAlpha(c);
    }

    private void toggleCards(boolean value)
    {
        for (AbstractCard c : cards.group)
        {
            toggleCardImpl(c, value);
        }
        refreshCountText();
    }

    private void toggleViewUpgrades(boolean value)
    {
        SingleCardViewPopup.isViewingUpgrade = value;
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
    }

    public void refreshCountText()
    {
        selectedCount.setLabel(EUIUtils.format(PGR.core.strings.seriesSelection.selected, EUIUtils.count(cards.group, card -> !screen.container.bannedCards.contains(card.cardID)), cards.group.size()));
        screen.forceUpdateText();
    }

    public void refresh(CardGroup cards)
    {
        this.cards = cards;
        this.grid = new EUICardGrid()
                .canDragScreen(false)
                .addCards(cards.group);
    }

    public PCLViewLoadoutPoolEffect setStartingPosition(float x, float y)
    {
        for (AbstractCard c : cards.group)
        {
            c.current_x = x - (c.hb.width * 0.5f);
            c.current_y = y - (c.hb.height * 0.5f);
        }

        return this;
    }

    public PCLViewLoadoutPoolEffect setCanToggle(boolean canToggle)
    {
        this.canToggle = canToggle;
        selectAllButton.setActive(canToggle);
        deselectAllButton.setActive(canToggle);
        selectedCount.setActive(canToggle);
        return this;
    }

    @Override
    public void render(SpriteBatch sb)
    {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        grid.tryRender(sb);
        upgradeToggle.renderImpl(sb);
        selectAllButton.tryRender(sb);
        deselectAllButton.tryRender(sb);
        selectedCount.tryRender(sb);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        grid.tryUpdate();
        upgradeToggle.updateImpl();
        selectAllButton.tryUpdate();
        deselectAllButton.tryUpdate();
        selectedCount.tryUpdate();

        if (upgradeToggle.hb.hovered || selectAllButton.hb.hovered || deselectAllButton.hb.hovered || grid.hoveredCard != null)
        {
            duration = startingDuration * 0.1f;
            isDone = false;
            return;
        }

        if (grid.scrollBar.isDragging)
        {
            duration = startingDuration;
            isDone = false;
            return;
        }

        if (tickDuration(deltaTime))
        {
            if (EUIInputManager.leftClick.isJustReleased() || EUIInputManager.rightClick.isJustReleased())
            {
                complete(this.cards);
                return;
            }

            isDone = false;
        }
    }

    @Override
    protected void complete()
    {
        super.complete();

        if (showTopPanelOnComplete)
        {
            GameUtilities.setTopPanelVisible(true);
            showTopPanelOnComplete = false;
        }
    }
}