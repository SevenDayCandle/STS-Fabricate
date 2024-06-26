package pinacolada.effects.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.RelicInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;
import pinacolada.ui.customRun.PCLRandomRelicAmountDialog;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static pinacolada.skills.PSkill.COLON_SEPARATOR;
import static pinacolada.utilities.GameUtilities.scale;
import static pinacolada.utilities.GameUtilities.screenW;

public class ViewInGameRelicPoolEffect extends PCLEffectWithCallback<ViewInGameRelicPoolEffect> {
    private final ActionT0 onRefresh;
    private final Color screenColor;
    private final EUIRelicGrid grid;
    public final ArrayList<AbstractRelic> relics;
    public final HashSet<String> bannedRelics;
    private EUIButton deselectAllButton;
    private EUIButton selectAllButton;
    private EUIButton selectCustomButton;
    private EUIButton selectRandomButton;
    private EUIButton importButton;
    private EUILabel selectedCount;
    private EUIToggle upgradeToggle;
    private PCLRandomRelicAmountDialog randomSelection;
    private boolean canToggle = true;
    private boolean draggingScreen;
    private boolean showTopPanelOnComplete;

    public ViewInGameRelicPoolEffect(ArrayList<AbstractRelic> relics, HashSet<String> bannedRelics) {
        this(relics, bannedRelics, null, false);
    }

    public ViewInGameRelicPoolEffect(ArrayList<AbstractRelic> relics, HashSet<String> bannedRelics, ActionT0 onRefresh, boolean disableLocks) {
        super(0.7f);

        this.bannedRelics = new HashSet<>();
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
            complete(this);
            return;
        }

        // Only allow cards in the actual grid to be amongst the bannedCards
        for (AbstractRelic c : relics) {
            if (bannedRelics.contains(c.relicId)) {
                this.bannedRelics.add(c.relicId);
            }
        }

        if (GameUtilities.isTopPanelVisible()) {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid = (EUIRelicGrid) new EUIRelicGrid()
                .canDragScreen(false)
                .setOnClick(this::toggleRelic);
        if (disableLocks) {
            this.grid.setItems(relics, r -> new RelicInfo(r, false));
        }
        else {
            this.grid.setItems(relics, RelicInfo::new);
        }

        for (RelicInfo c : grid.group) {
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

        importButton = EUIButton.createHexagonalButton(xPos, selectRandomButton.hb.y - selectRandomButton.hb.height, buttonWidth, buttonHeight)
                .setText(PGR.core.strings.sui_importFromFile)
                .setOnClick(this::importFromFile)
                .setColor(Color.ROYAL);

        upgradeToggle = new EUIToggle(new EUIHitbox(xPos, importButton.hb.y - importButton.hb.height * 2.5f, buttonWidth, buttonHeight))
                .setBackground(EUIRM.images.greySquare.texture(), Color.DARK_GRAY)
                .setFont(FontHelper.cardDescFont_L, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(this::toggleViewUpgrades);

        randomSelection = (PCLRandomRelicAmountDialog) new PCLRandomRelicAmountDialog(PGR.core.strings.sui_selectRandom)
                .setOnComplete((this::selectRandomRelics))
                .setActive(false);

        EUI.toggleViewUpgrades(false);
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
        refreshCountText();

        EUI.relicFilters.initializeForSort(grid.group, __ -> {
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, AbstractCard.CardColor.COLORLESS);
    }

    @Override
    protected void complete() {
        super.complete();

        if (showTopPanelOnComplete) {
            GameUtilities.setTopPanelVisible(true);
            showTopPanelOnComplete = false;
        }
    }

    private void importFromFile() {
        try {
            File openedFile = EUIUtils.loadFile(ViewInGameCardPoolEffect.EXTENSIONS, PGR.config.lastCSVPath);
            FileHandle fh = Gdx.files.absolute(openedFile.getAbsolutePath());
            String[] lines = EUIUtils.splitString(EUIExporter.NEWLINE, fh.readString());
            bannedRelics.clear();

            HashMap<String, RelicInfo> cardMap = new HashMap<>();
            for (RelicInfo c : grid.group) {
                cardMap.put(c.relic.relicId, c);
                bannedRelics.add(c.relic.relicId);
            }

            // ID is the first delimited object per line. Assume comma delineation
            // Skip the header row
            for (int start = EUIExporter.EXT_CSV.equals(fh.extension()) ? 1 : 0; start < lines.length; start++) {
                String id = EUIUtils.splitString(",",lines[start])[0];
                RelicInfo found = cardMap.get(id);
                if (found != null) {
                    bannedRelics.remove(id);
                }
            }

            for (RelicInfo c : grid.group) {
                updateRelicAlpha(c);
            }

            refreshCountText();
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(this, "Failed to load CSV file: " + e.getLocalizedMessage());
        }
    }

    public void refreshCountText() {
        if (canToggle) {
            selectedCount.setLabel(EUIUtils.format(PGR.core.strings.sui_selected, EUIUtils.count(relics, relic -> !bannedRelics.contains(relic.relicId)), relics.size()));
        }
        else {
            selectedCount.setLabel(EUIRM.strings.ui_total + COLON_SEPARATOR + relics.size());
        }
        if (onRefresh != null) {
            onRefresh.invoke();
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
        EUI.sortHeader.render(sb);
        if (!EUI.relicFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
            EUIExporter.exportButton.tryRender(sb);
        }
        if (randomSelection.isActive) {
            sb.setColor(this.screenColor);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        }
        randomSelection.tryRender(sb);
    }

    private void selectRandomRelics(PCLRandomRelicAmountDialog dialog) {
        randomSelection.setActive(false);
        if (dialog != null) {
            bannedRelics.clear();
            RandomizedList<RelicInfo> possibleCards = new RandomizedList<>(grid.group.group);

            while (possibleCards.size() > dialog.getCount()) {
                RelicInfo c = possibleCards.retrieveUnseeded(true);
                if (c != null) {
                    toggleRelicImpl(c, bannedRelics.contains(c.relic.relicId));
                }
            }

            for (RelicInfo c : possibleCards) {
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
            toggleRelicImpl(c, bannedRelics.contains(c.relic.relicId));
            refreshCountText();
        }
    }

    private void toggleRelicImpl(RelicInfo c, boolean value) {
        if (value) {
            bannedRelics.remove(c.relic.relicId);
        }
        else {
            bannedRelics.add(c.relic.relicId);
        }
        updateRelicAlpha(c);
    }

    private void toggleRelics(boolean value) {
        for (RelicInfo c : grid.group) {
            toggleRelicImpl(c, value);
        }
        refreshCountText();
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        boolean wasFiltersOpen = EUI.relicFilters.isActive;
        EUI.relicFilters.tryUpdate();
        boolean shouldDoStandardUpdate = !wasFiltersOpen && !randomSelection.tryUpdate() && !EUIExporter.exportDropdown.isOpen();
        if (shouldDoStandardUpdate) {
            EUI.openFiltersButton.tryUpdate();
            EUIExporter.exportButton.tryUpdate();
            EUI.sortHeader.update();
            grid.tryUpdate();
            upgradeToggle.updateImpl();
            selectAllButton.tryUpdate();
            deselectAllButton.tryUpdate();
            selectRandomButton.tryUpdate();
            selectedCount.tryUpdate();

            if (upgradeToggle.hb.hovered || selectAllButton.hb.hovered || deselectAllButton.hb.hovered
                    || selectRandomButton.hb.hovered || importButton.hb.hovered || grid.isHovered() || EUI.sortHeader.isHovered() || EUI.openFiltersButton.hb.hovered || EUIExporter.exportButton.hb.hovered) {
                return;
            }

            if (EUIInputManager.leftClick.isJustPressed() || EUIInputManager.rightClick.isJustPressed()) {
                complete(this);
            }
        }
        EUIExporter.exportDropdown.tryUpdate();
    }

    private void updateRelicAlpha(RelicInfo c) {
        c.faded = bannedRelics.contains(c.relic.relicId);
    }
}