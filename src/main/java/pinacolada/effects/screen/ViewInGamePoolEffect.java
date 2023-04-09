package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIStaticCardGrid;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.HashSet;

import static pinacolada.utilities.GameUtilities.scale;
import static pinacolada.utilities.GameUtilities.screenW;

public class ViewInGamePoolEffect extends PCLEffectWithCallback<CardGroup>
{
    private EUIButton deselectAllButton;
    private EUIButton selectAllButton;
    private EUIButton selectCustomButton;
    private EUIButton selectRandomButton;
    private EUILabel selectedCount;
    private EUIToggle upgradeToggle;
    private boolean canToggle = true;
    private boolean draggingScreen;
    private boolean showTopPanelOnComplete;
    private final ActionT0 onRefresh;
    private final CardGroup cards;
    private final Color screenColor;
    private final EUIStaticCardGrid grid;
    private final HashSet<String> bannedCards;

    public ViewInGamePoolEffect(CardGroup cards, HashSet<String> bannedCards)
    {
        this(cards, bannedCards, null);
    }

    public ViewInGamePoolEffect(CardGroup cards, HashSet<String> bannedCards, ActionT0 onRefresh)
    {
        super(0.7f);

        this.bannedCards = bannedCards;
        this.onRefresh = onRefresh;
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
            this.grid = (EUIStaticCardGrid) new EUIStaticCardGrid().canDragScreen(false);
            complete(cards);
            return;
        }

        if (GameUtilities.isTopPanelVisible())
        {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid = (EUIStaticCardGrid) new EUIStaticCardGrid()
                .canRenderUpgrades(true)
                .canDragScreen(false)
                .setOnCardClick(this::toggleCard);
        this.grid.setCardGroup(cards);
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
                .setText(PGR.core.strings.sui_selectAll)
                .setPosition(xPos, Settings.HEIGHT * 0.65f)
                .setOnClick(() -> this.toggleCards(true))
                .setColor(Color.ROYAL);

        deselectAllButton =  AbstractScreen.createHexagonalButton(xPos, selectAllButton.hb.y - selectAllButton.hb.height, buttonWidth, buttonHeight)
                .setText(PGR.core.strings.sui_deselectAll)
                .setPosition(xPos, selectAllButton.hb.y - selectAllButton.hb.height)
                .setOnClick(() -> this.toggleCards(false))
                .setColor(Color.FIREBRICK);

        upgradeToggle = new EUIToggle(new EUIHitbox(buttonWidth, buttonHeight))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setPosition(xPos, deselectAllButton.hb.y - deselectAllButton.hb.height * 2)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(this::toggleViewUpgrades);

        EUI.toggleViewUpgrades(false);
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
        refreshCountText();

        EUI.cardFilters.initializeForCustomHeader(grid.cards, __ -> {
            grid.moveToTop();
            grid.forceUpdateCardPositions();
        }, AbstractCard.CardColor.COLORLESS, false);
    }

    private void updateCardAlpha(AbstractCard c)
    {
        c.transparency = c.targetTransparency = bannedCards.contains(c.cardID) ? 0.35f : 1f;
        AbstractCard upgrade = grid.getUpgrade(c);
        {
            if (upgrade != null)
            {
                upgrade.transparency = upgrade.targetTransparency = c.targetTransparency;
                upgrade.update();
            }
        }
    }

    private void toggleCard(AbstractCard c)
    {
        if (canToggle)
        {
            toggleCardImpl(c, bannedCards.contains(c.cardID));
            refreshCountText();
        }
    }

    private void toggleCardImpl(AbstractCard c, boolean value)
    {
        if (value)
        {
            bannedCards.remove(c.cardID);
        }
        else {
            bannedCards.add(c.cardID);
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
        selectedCount.setLabel(EUIUtils.format(PGR.core.strings.sui_selected, EUIUtils.count(cards.group, card -> !bannedCards.contains(card.cardID)), cards.group.size()));
        if (onRefresh != null)
        {
            onRefresh.invoke();
        }
    }

    public ViewInGamePoolEffect setStartingPosition(float x, float y)
    {
        for (AbstractCard c : cards.group)
        {
            c.current_x = x - (c.hb.width * 0.5f);
            c.current_y = y - (c.hb.height * 0.5f);
        }

        return this;
    }

    public ViewInGamePoolEffect setCanToggle(boolean canToggle)
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
        EUI.customHeader.render(sb);
        if (!EUI.cardFilters.isActive) {
            EUI.openCardFiltersButton.tryRender(sb);
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        boolean shouldDoStandardUpdate = !EUI.cardFilters.tryUpdate();
        if (shouldDoStandardUpdate)
        {
            EUI.openCardFiltersButton.tryUpdate();
            EUI.customHeader.update();
            grid.tryUpdate();
            upgradeToggle.updateImpl();
            selectAllButton.tryUpdate();
            deselectAllButton.tryUpdate();
            selectedCount.tryUpdate();

            if (upgradeToggle.hb.hovered || selectAllButton.hb.hovered || deselectAllButton.hb.hovered || grid.isHovered() || EUI.customHeader.isHovered() || EUI.openCardFiltersButton.hb.hovered)
            {
                return;
            }

            if (EUIInputManager.leftClick.isJustPressed() || EUIInputManager.rightClick.isJustPressed())
            {
                complete(this.cards);
            }
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