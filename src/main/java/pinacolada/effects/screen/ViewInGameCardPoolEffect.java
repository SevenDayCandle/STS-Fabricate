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
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;
import pinacolada.ui.customRun.PCLRandomCardAmountDialog;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static pinacolada.skills.PSkill.COLON_SEPARATOR;
import static pinacolada.utilities.GameUtilities.scale;
import static pinacolada.utilities.GameUtilities.screenW;

public class ViewInGameCardPoolEffect extends PCLEffectWithCallback<ViewInGameCardPoolEffect> {
    protected static final FileNameExtensionFilter EXTENSIONS = new FileNameExtensionFilter("*.csv", "csv");
    private final ActionT0 onRefresh;
    private final Color screenColor;
    private final EUICardGrid grid;
    public final ArrayList<AbstractCard> cards;
    public final HashSet<String> bannedCards;
    private EUIButton deselectAllButton;
    private EUIButton selectAllButton;
    private EUIButton selectRandomButton;
    private EUIButton importButton;
    private EUILabel selectedCount;
    private EUITextBox instructions;
    private EUIToggle upgradeToggle;
    private PCLRandomCardAmountDialog randomSelection;
    private boolean canToggle = true;
    private boolean draggingScreen;
    private boolean showTopPanelOnComplete;

    public ViewInGameCardPoolEffect(ArrayList<AbstractCard> cards, HashSet<String> bannedCards) {
        this(cards, bannedCards, null);
    }

    public ViewInGameCardPoolEffect(ArrayList<AbstractCard> cards, HashSet<String> bannedCards, ActionT0 onRefresh) {
        super(0.7f);

        this.onRefresh = onRefresh;
        this.cards = cards;
        this.bannedCards = new HashSet<>();
        this.isRealtime = true;
        this.screenColor = Color.BLACK.cpy();
        this.screenColor.a = 0.8f;

        if (GameUtilities.inGame()) {
            AbstractDungeon.overlayMenu.proceedButton.hide();
        }

        if (cards.isEmpty()) {
            this.grid = (EUICardGrid) new EUICardGrid().canDragScreen(false);
            complete(this);
            return;
        }

        // Only allow cards in the actual grid to be amongst the bannedCards
        for (AbstractCard c : cards) {
            if (bannedCards.contains(c.cardID)) {
                this.bannedCards.add(c.cardID);
            }
        }

        if (GameUtilities.isTopPanelVisible()) {
            showTopPanelOnComplete = true;
            GameUtilities.setTopPanelVisible(false);
        }

        this.grid = (EUICardGrid) new EUICardGrid()
                .setCanRenderUpgrades(true)
                .canDragScreen(false)
                .setOnClick(this::toggleCard);
        this.grid.setItems(cards);
        for (AbstractCard c : cards) {
            updateCardAlpha(c);
        }

        final float xPos = screenW(0.005f);
        final float buttonWidth = scale(256);
        final float buttonHeight = scale(48);

        selectedCount = new EUILabel(FontHelper.tipHeaderFont, new EUIHitbox(xPos, Settings.HEIGHT * 0.85f, buttonWidth, buttonHeight * 2f))
                .setColor(Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f, true);

        instructions = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(xPos, Settings.HEIGHT * 0.65f, buttonWidth, Settings.HEIGHT * 0.18f))
                .setLabel(PGR.core.strings.sui_viewPoolInstructions)
                .setAlignment(0.9f, 0.1f, true)
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.tooltipFont, 1f);

        deselectAllButton = EUIButton.createHexagonalButton(xPos, Settings.HEIGHT * 0.54f, buttonWidth, buttonHeight)
                .setText(PGR.core.strings.sui_deselectAll)
                .setOnClick(() -> this.toggleCards(false))
                .setColor(Color.FIREBRICK);

        selectAllButton = EUIButton.createHexagonalButton(xPos, deselectAllButton.hb.y - deselectAllButton.hb.height, buttonWidth, buttonHeight)
                .setText(PGR.core.strings.sui_selectAll)
                .setOnClick(() -> this.toggleCards(true))
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

        randomSelection = (PCLRandomCardAmountDialog) new PCLRandomCardAmountDialog(PGR.core.strings.sui_selectRandom)
                .setOnComplete((this::selectRandomCards))
                .setActive(false);

        EUI.toggleViewUpgrades(false);
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
        refreshCountText();

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

    private void importFromFile() {
        try {
            File openedFile = EUIUtils.loadFile(EXTENSIONS, PGR.config.lastCSVPath);
            FileHandle fh = Gdx.files.absolute(openedFile.getAbsolutePath());
            String[] lines = EUIUtils.splitString(EUIExporter.NEWLINE, fh.readString());
            bannedCards.clear();

            HashMap<String, AbstractCard> cardMap = new HashMap<>();
            for (AbstractCard c : grid.group) {
                cardMap.put(c.cardID, c);
                bannedCards.add(c.cardID);
            }

            // ID is the first delimited object per line. Assume comma delineation
            // Skip the header row
            for (int start = EUIExporter.EXT_CSV.equals(fh.extension()) ? 1 : 0; start < lines.length; start++) {
                String id = EUIUtils.splitString(",",lines[start])[0];
                AbstractCard found = cardMap.get(id);
                if (found != null) {
                    bannedCards.remove(id);
                }
            }

            for (AbstractCard c : grid.group) {
                updateCardAlpha(c);
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
            selectedCount.setLabel(EUIUtils.format(PGR.core.strings.sui_selected, EUIUtils.count(cards, card -> !bannedCards.contains(card.cardID)), cards.size()));
        }
        else {
            selectedCount.setLabel(EUIRM.strings.ui_total + COLON_SEPARATOR + cards.size());
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
        instructions.tryRender(sb);
        upgradeToggle.renderImpl(sb);
        selectAllButton.tryRender(sb);
        deselectAllButton.tryRender(sb);
        selectRandomButton.tryRender(sb);
        importButton.tryRender(sb);
        selectedCount.tryRender(sb);
        EUI.sortHeader.render(sb);
        if (!EUI.cardFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
            EUIExporter.exportButton.tryRender(sb);
        }
        if (randomSelection.isActive) {
            sb.setColor(this.screenColor);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        }
        randomSelection.tryRender(sb);
    }

    private void selectRandomCards(PCLRandomCardAmountDialog dialog) {
        randomSelection.setActive(false);
        if (dialog != null) {
            bannedCards.clear();
            RandomizedList<AbstractCard> possibleCards = new RandomizedList<>();
            RandomizedList<AbstractCard> possibleColorless = new RandomizedList<>();
            RandomizedList<AbstractCard> possibleCurses = new RandomizedList<>();
            for (AbstractCard c : cards) {
                if (c.color == AbstractCard.CardColor.COLORLESS) {
                    possibleColorless.add(c);
                }
                else if (c.color == AbstractCard.CardColor.CURSE) {
                    possibleCurses.add(c);
                }
                else {
                    possibleCards.add(c);
                }
            }

            while (possibleCards.size() > dialog.getCardCount()) {
                AbstractCard c = possibleCards.retrieveUnseeded(true);
                if (c != null) {
                    toggleCardImpl(c, bannedCards.contains(c.cardID));
                }
            }
            while (possibleColorless.size() > dialog.getColorlessCount()) {
                AbstractCard c = possibleColorless.retrieveUnseeded(true);
                if (c != null) {
                    toggleCardImpl(c, bannedCards.contains(c.cardID));
                }
            }
            while (possibleCurses.size() > dialog.getCurseCount()) {
                AbstractCard c = possibleCurses.retrieveUnseeded(true);
                if (c != null) {
                    toggleCardImpl(c, bannedCards.contains(c.cardID));
                }
            }

            for (AbstractCard c : possibleCards) {
                updateCardAlpha(c);
            }
            for (AbstractCard c : possibleColorless) {
                updateCardAlpha(c);
            }
            for (AbstractCard c : possibleCurses) {
                updateCardAlpha(c);
            }
            refreshCountText();
        }
    }

    public ViewInGameCardPoolEffect setCanToggle(boolean canToggle) {
        this.canToggle = canToggle;
        selectAllButton.setActive(canToggle);
        deselectAllButton.setActive(canToggle);
        selectRandomButton.setActive(canToggle);
        importButton.setActive(canToggle);
        instructions.setActive(canToggle);
        return this;
    }

    public ViewInGameCardPoolEffect setStartingPosition(float x, float y) {
        for (AbstractCard c : cards) {
            c.current_x = x - (c.hb.width * 0.5f);
            c.current_y = y - (c.hb.height * 0.5f);
        }

        return this;
    }

    private void startSelectRandom() {
        randomSelection.open(cards);
    }

    private void toggleCard(AbstractCard c) {
        if (canToggle) {
            toggleCardImpl(c, bannedCards.contains(c.cardID));
            refreshCountText();
        }
    }

    private void toggleCardImpl(AbstractCard c, boolean value) {
        if (value) {
            bannedCards.remove(c.cardID);
        }
        else {
            bannedCards.add(c.cardID);
        }
        updateCardAlpha(c);
    }

    private void toggleCards(boolean value) {
        for (AbstractCard c : cards) {
            toggleCardImpl(c, value);
        }
        refreshCountText();
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
    }

    private void updateCardAlpha(AbstractCard c) {
        c.transparency = c.targetTransparency = bannedCards.contains(c.cardID) ? 0.35f : 1f;
        AbstractCard upgrade = grid.getUpgrade(c);
        {
            if (upgrade != null) {
                upgrade.transparency = upgrade.targetTransparency = c.targetTransparency;
                upgrade.update();
            }
        }
    }

    @Override
    protected void updateInternal(float deltaTime) {
        boolean wasFiltersOpen = EUI.cardFilters.isActive;
        EUI.cardFilters.tryUpdate();
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
            importButton.tryUpdate();
            instructions.tryUpdate();
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
}