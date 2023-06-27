package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.*;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.RelicInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;
import pinacolada.ui.customRun.PCLRandomRelicAmountDialog;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;
import java.util.HashSet;

import static pinacolada.utilities.GameUtilities.scale;
import static pinacolada.utilities.GameUtilities.screenW;

public class ViewInGameRelicPoolEffect extends PCLEffectWithCallback<ArrayList<AbstractRelic>> {
    private final ActionT0 onRefresh;
    private final ArrayList<AbstractRelic> relics;
    private final Color screenColor;
    private final EUIRelicGrid grid;
    private final HashSet<String> bannedRelics;
    private EUIButton deselectAllButton;
    private EUIButton selectAllButton;
    private EUIButton selectCustomButton;
    private EUIButton selectRandomButton;
    private EUILabel selectedCount;
    private EUIToggle upgradeToggle;
    private PCLRandomRelicAmountDialog randomSelection;
    private boolean canToggle = true;
    private boolean draggingScreen;
    private boolean showTopPanelOnComplete;

    public ViewInGameRelicPoolEffect(ArrayList<AbstractRelic> relics, HashSet<String> bannedRelics) {
        this(relics, bannedRelics, null);
    }

    public ViewInGameRelicPoolEffect(ArrayList<AbstractRelic> relics, HashSet<String> bannedRelics, ActionT0 onRefresh) {
        super(0.7f);

        this.bannedRelics = bannedRelics;
        this.onRefresh = onRefresh;
        this.relics = relics;
        this.isRealtime = true;
        this.screenColor = Color.BLACK.cpy();
        this.screenColor.a = 0.8f;

        if (GameUtilities.inGame()) {
            AbstractDungeon.overlayMenu.proceedButton.hide();
        }

        if (relics.isEmpty()) {
            this.grid = (EUIRelicGrid) new EUIRelicGrid().canDragScreen(false);
            complete(relics);
            return;
        }

        if (GameUtilities.isTopPanelVisible()) {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid = (EUIRelicGrid) new EUIRelicGrid()
                .canDragScreen(false)
                .setOnClick(this::toggleRelic);
        this.grid.setItems(relics, RelicInfo::new);
        for (AbstractRelic c : relics) {
            updateRelicAlpha(c);
        }

        final float xPos = screenW(0.005f);
        final float buttonWidth = scale(256);
        final float buttonHeight = scale(48);

        selectedCount = new EUILabel(FontHelper.tipHeaderFont, new EUIHitbox(xPos, Settings.HEIGHT * 0.75f, buttonWidth, buttonHeight * 2f))
                .setColor(Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f, true)
                .setFont(FontHelper.tipHeaderFont, 1);

        deselectAllButton = EUIButton.createHexagonalButton(xPos, Settings.HEIGHT * 0.65f, buttonWidth, buttonHeight)
                .setText(PGR.core.strings.sui_deselectAll)
                .setOnClick(() -> this.toggleRelics(false))
                .setColor(Color.FIREBRICK);

        selectAllButton = EUIButton.createHexagonalButton(xPos, deselectAllButton.hb.y - deselectAllButton.hb.height, buttonWidth, buttonHeight)
                .setText(PGR.core.strings.sui_selectAll)
                .setOnClick(() -> this.toggleRelics(true))
                .setColor(Color.ROYAL);

        selectRandomButton = EUIButton.createHexagonalButton(xPos, selectAllButton.hb.y - selectAllButton.hb.height, buttonWidth, buttonHeight)
                .setText(PGR.core.strings.sui_selectRandom)
                .setOnClick(this::startSelectRandom)
                .setColor(Color.ROYAL);

        upgradeToggle = new EUIToggle(new EUIHitbox(xPos, selectRandomButton.hb.y - selectRandomButton.hb.height * 3, buttonWidth, buttonHeight))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(this::toggleViewUpgrades);

        randomSelection = (PCLRandomRelicAmountDialog) new PCLRandomRelicAmountDialog(PGR.core.strings.sui_selectRandom)
                .setOnComplete((this::selectRandomRelics))
                .setActive(false);

        EUI.toggleViewUpgrades(false);
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
        refreshCountText();

        EUI.relicFilters.initializeForCustomHeader(grid.group, __ -> {
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, AbstractCard.CardColor.COLORLESS, false, false);
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
        upgradeToggle.renderImpl(sb);
        selectAllButton.tryRender(sb);
        deselectAllButton.tryRender(sb);
        selectRandomButton.tryRender(sb);
        selectedCount.tryRender(sb);
        EUI.relicHeader.render(sb);
        if (!EUI.relicFilters.isActive) {
            EUI.openRelicFiltersButton.tryRender(sb);
        }
        if (randomSelection.isActive) {
            sb.setColor(this.screenColor);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        }
        randomSelection.tryRender(sb);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        boolean shouldDoStandardUpdate = !EUI.relicFilters.tryUpdate() && !randomSelection.tryUpdate();
        if (shouldDoStandardUpdate) {
            EUI.openCardFiltersButton.tryUpdate();
            EUI.relicHeader.update();
            grid.tryUpdate();
            upgradeToggle.updateImpl();
            selectAllButton.tryUpdate();
            deselectAllButton.tryUpdate();
            selectRandomButton.tryUpdate();
            selectedCount.tryUpdate();

            if (upgradeToggle.hb.hovered || selectAllButton.hb.hovered || deselectAllButton.hb.hovered || selectRandomButton.hb.hovered || grid.isHovered() || EUI.openRelicFiltersButton.hb.hovered) {
                return;
            }

            if (EUIInputManager.leftClick.isJustPressed() || EUIInputManager.rightClick.isJustPressed()) {
                complete(this.relics);
            }
        }
    }

    public void refreshCountText() {
        selectedCount.setLabel(EUIUtils.format(PGR.core.strings.sui_selected, EUIUtils.count(relics, relic -> !bannedRelics.contains(relic.relicId)), relics.size()));
        if (onRefresh != null) {
            onRefresh.invoke();
        }
    }

    private void selectRandomRelics(PCLRandomRelicAmountDialog dialog) {
        randomSelection.setActive(false);
        if (dialog != null) {
            bannedRelics.clear();
            RandomizedList<AbstractRelic> possibleCards = new RandomizedList<>();
            RandomizedList<AbstractRelic> possibleColorless = new RandomizedList<>();
            for (AbstractRelic c : relics) {
                if (EUIGameUtils.getRelicColor(c.relicId) == AbstractCard.CardColor.COLORLESS) {
                    possibleColorless.add(c);
                }
                else {
                    possibleCards.add(c);
                }
            }

            while (possibleCards.size() > dialog.getCardCount()) {
                AbstractRelic c = possibleCards.retrieveUnseeded(true);
                if (c != null) {
                    toggleRelicImpl(c, bannedRelics.contains(c.relicId));
                }
            }
            while (possibleColorless.size() > dialog.getColorlessCount()) {
                AbstractRelic c = possibleColorless.retrieveUnseeded(true);
                if (c != null) {
                    toggleRelicImpl(c, bannedRelics.contains(c.relicId));
                }
            }

            for (AbstractRelic c : possibleCards) {
                updateRelicAlpha(c);
            }
            for (AbstractRelic c : possibleColorless) {
                updateRelicAlpha(c);
            }

            refreshCountText();
        }
    }

    public ViewInGameRelicPoolEffect setCanToggle(boolean canToggle) {
        this.canToggle = canToggle;
        selectAllButton.setActive(canToggle);
        deselectAllButton.setActive(canToggle);
        selectedCount.setActive(canToggle);
        return this;
    }

    public ViewInGameRelicPoolEffect setStartingPosition(float x, float y) {
        for (AbstractRelic c : relics) {
            c.currentX = x - (c.hb.width * 0.5f);
            c.currentY = y - (c.hb.height * 0.5f);
        }

        return this;
    }

    private void startSelectRandom() {
        randomSelection.open(relics);
    }

    private void toggleRelic(RelicInfo c) {
        if (canToggle) {
            toggleRelicImpl(c.relic, bannedRelics.contains(c.relic.relicId));
            refreshCountText();
        }
    }

    private void toggleRelicImpl(AbstractRelic c, boolean value) {
        if (value) {
            bannedRelics.remove(c.relicId);
        }
        else {
            bannedRelics.add(c.relicId);
        }
        updateRelicAlpha(c);
    }

    private void toggleRelics(boolean value) {
        for (AbstractRelic c : relics) {
            toggleRelicImpl(c, value);
        }
        refreshCountText();
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
    }

    private void updateRelicAlpha(AbstractRelic c) {
        c.grayscale = bannedRelics.contains(c.relicId);
    }
}