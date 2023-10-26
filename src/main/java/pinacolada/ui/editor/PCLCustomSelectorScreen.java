package pinacolada.ui.editor;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.cardFilter.GenericFiltersObject;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.PCLCustomCopyConfirmationEffect;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.resources.PGR;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PCLCustomSelectorScreen<T, U extends PCLCustomEditorLoadable<?, ?>, V extends GenericFiltersObject> extends AbstractMenuScreen {
    private static final float DRAW_START_X = (Settings.WIDTH - (5f * AbstractCard.IMG_WIDTH * 0.75f) - (4f * Settings.CARD_VIEW_PAD_X) + AbstractCard.IMG_WIDTH * 0.75f);
    private static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    private static final float PAD_Y = AbstractCard.IMG_HEIGHT * 0.75f + Settings.CARD_VIEW_PAD_Y;
    private static final float SCROLL_BAR_THRESHOLD = 500f * Settings.scale;
    protected static final float ITEM_HEIGHT = AbstractCard.IMG_HEIGHT * 0.15f;
    public static AbstractCard.CardColor currentColor = AbstractCard.CardColor.COLORLESS;
    public static AbstractPlayer.PlayerClass currentClass;
    protected final EUIItemGrid<T> grid;
    protected final EUIToggle toggle;
    private T clicked;
    private ArrayList<T> current;
    protected EUIButton addButton;
    protected EUIButton cancelButton;
    protected EUIButton loadExistingButton;
    protected EUIButton openButton;
    protected EUIButton reloadButton;
    protected EUIButtonList colorButtons;
    protected EUIContextMenu<ContextOption> contextMenu;
    protected EUITextBox info;
    protected HashMap<T, U> currentSlots = new HashMap<>();
    protected PCLEffectWithCallback<?> currentDialog;
    protected V savedFilters;

    public PCLCustomSelectorScreen() {
        final float buttonHeight = screenH(0.06f);
        final float labelHeight = screenH(0.04f);
        final float buttonWidth = screenW(0.18f);
        final float labelWidth = screenW(0.20f);

        this.savedFilters = getSavedFilters();
        this.grid = getGrid()
                .setOnClick(this::onClicked)
                .setOnRightClick(this::onRightClicked);
        toggle = new EUIToggle(new EUIHitbox(0, 0, AbstractCard.IMG_WIDTH * 0.2f, ITEM_HEIGHT))
                .setBackground(EUIRM.images.greySquare.texture(), Color.DARK_GRAY)
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.65f)
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.5f);
        cancelButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(buttonWidth * 0.53f, buttonHeight)
                .setColor(Color.FIREBRICK)
                .setText(GridCardSelectScreen.TEXT[1])
                .setOnClick(this::close);

        addButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.FOREST)
                .setText(PGR.core.strings.cedit_newCard)
                .setOnClick(this::add);

        loadExistingButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(addButton.hb.cX, addButton.hb.y + addButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.SKY)
                .setText(PGR.core.strings.cedit_loadFromCard)
                .setTooltip(PGR.core.strings.cedit_loadFromCard, PGR.core.strings.cetut_loadFromCardScreen)
                .setOnClick(this::loadFromExisting);

        openButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(loadExistingButton.hb.cX, loadExistingButton.hb.y + loadExistingButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_openFolder)
                .setTooltip(PGR.core.strings.cedit_openFolder, PGR.core.strings.cetut_openFolder)
                .setOnClick(this::viewDesktopFolderBase);

        reloadButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(openButton.hb.cX, openButton.hb.y + openButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_reloadCards)
                .setTooltip(PGR.core.strings.cedit_reloadCards, PGR.core.strings.cetut_selectorReload)
                .setOnClick(this::onReload);

        contextMenu = (EUIContextMenu<ContextOption>) new EUIContextMenu<ContextOption>(new EUIHitbox(-500f, -500f, 0, 0), o -> o.name)
                .setItems(getContextOptions())
                .setOnChange(options -> {
                    for (ContextOption o : options) {
                        o.onSelect.invoke(this);
                    }
                })
                .setCanAutosizeButton(true);
        info = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(screenW(0.25f), screenH(0.022f), screenW(0.5f), buttonHeight * 2f))
                .setLabel(PGR.core.strings.cetut_selector2)
                .setAlignment(0.75f, 0.05f, true)
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 0.9f);

        colorButtons = new EUIButtonList(14, screenW(0.09f), screenH(0.95f), EUIButtonList.BUTTON_W, EUIButtonList.BUTTON_H);

        for (AbstractCard.CardColor color : getAllColors()) {
            makeColorButton(color);
        }
    }

    public static void viewDesktopFolder() {
        viewDesktopFolder(Gdx.files.local(PCLCustomLoadable.FOLDER));
    }

    public static void viewDesktopFolder(FileHandle handle) {
        try {
            Desktop.getDesktop().open(handle.file());
        }
        catch (Exception e) {
            EUIUtils.logError(null, "Failed to open card folder.");
        }
    }

    public void add() {
        if (currentDialog == null) {
            U slot = makeSlot(currentColor);
            savedFilters.cloneFrom(getFilters().filters);
            currentDialog = getScreen(slot)
                    .setOnSave(() -> {
                        onAdd(slot);
                        putInList(slot);
                    });
        }
    }

    public void duplicate() {
        U sourceSlot = currentSlots.get(clicked);
        if (currentDialog == null && sourceSlot != null) {
            U slot = makeSlot(sourceSlot);
            savedFilters.cloneFrom(getFilters().filters);
            currentDialog = getScreen(slot)
                    .setOnSave(() -> {
                        onAdd(slot);
                        putInList(slot);
                    });
        }
    }

    public void duplicateToColor() {
        U sourceSlot = currentSlots.get(clicked);
        if (currentDialog == null && sourceSlot != null) {
            savedFilters.cloneFrom(getFilters().filters);
            currentDialog = new PCLCustomCopyConfirmationEffect(getAllColors())
                    .addCallback((co) -> {
                        if (co != null) {
                            U slot = makeSlot(sourceSlot, co);
                            openImpl(null, co);
                            currentDialog = getScreen(slot)
                                    .setOnSave(() -> {
                                        onAdd(slot);
                                    });
                        }
                    });
        }
    }

    public void edit(T item, U sourceSlot) {
        if (currentDialog == null && sourceSlot != null) {
            String oldID = sourceSlot.ID;
            savedFilters.cloneFrom(getFilters().filters);
            currentDialog = getScreen(sourceSlot)
                    .setOnSave(() -> {
                        onEdit(sourceSlot, oldID);
                        grid.group.group = current;
                        current.remove(item);
                        currentSlots.remove(item);
                        putInList(sourceSlot);
                    });
        }
    }

    protected List<AbstractCard.CardColor> getAllColors() {
        ArrayList<AbstractCard.CardColor> list = new ArrayList<>();

        // Base game colors are not tracked in getCardColors
        list.add(AbstractCard.CardColor.COLORLESS);
        list.add(AbstractCard.CardColor.RED);
        list.add(AbstractCard.CardColor.GREEN);
        list.add(AbstractCard.CardColor.BLUE);
        list.add(AbstractCard.CardColor.PURPLE);

        list.addAll(BaseMod.getCardColors().stream().sorted(Comparator.comparing(EUIGameUtils::getColorName)).collect(Collectors.toList()));
        return list;
    }

    protected ContextOption[] getContextOptions() {
        U slot = currentSlots.get(clicked);
        return slot != null && slot.getIsInternal()
                ? EUIUtils.array(ContextOption.Duplicate, ContextOption.DuplicateToColor, ContextOption.OpenFolder)
                : ContextOption.values();
    }

    protected EUITourTooltip[] getTour() {
        return EUIUtils.array(
                addButton.makeTour(true),
                loadExistingButton.makeTour(true),
                openButton.makeTour(true),
                reloadButton.makeTour(true)
        );
    }

    public void loadFromExisting() {
    }

    private void makeColorButton(AbstractCard.CardColor co) {
        colorButtons.addButton(button -> open(null, co), EUIGameUtils.getColorName(co))
                .setColor(EUIGameUtils.getColorColor(co));
    }

    private void onClicked(T item) {
        U slot = currentSlots.get(item);
        if (slot != null) {
            edit(item, slot);
        }
    }

    private void onRightClicked(T card) {
        clicked = card;
        contextMenu.positionToOpen();
    }

    public void open(AbstractPlayer.PlayerClass playerClass, AbstractCard.CardColor cardColor) {
        super.open();
        savedFilters.clear(true);
        openImpl(playerClass, cardColor);
        PGR.helpMeButton.setOnClick(() -> EUITourTooltip.queueTutorial(getTour()));
        EUITourTooltip.queueFirstView(PGR.config.tourItemScreen, getTour());
    }

    protected void openImpl(AbstractPlayer.PlayerClass playerClass, AbstractCard.CardColor cardColor) {
        currentClass = playerClass;
        currentColor = EUI.actingColor = cardColor;
        currentSlots.clear();
        grid.clear();
        for (U slot : getSlots(currentColor)) {
            T item = makeItem(slot);
            currentSlots.put(item, slot);
            grid.add(item);
        }
        current = grid.group.group;
        getFilters().initializeForSort(grid.group, __ -> {
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, currentColor);
        getFilters().cloneFrom(savedFilters);
    }

    protected void putInList(U slot) {
        T item = makeItem(slot);
        currentSlots.put(item, slot);
        grid.group.group = current;
        grid.add(item);
        getFilters().initializeForSort(grid.group, __ -> {
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, currentColor);
        getFilters().cloneFrom(savedFilters);
    }

    public void remove() {
        U sourceSlot = currentSlots.get(clicked);
        if (currentDialog == null && sourceSlot != null) {
            savedFilters.cloneFrom(getFilters().filters);
            currentDialog = new PCLCustomDeletionConfirmationEffect<U>(sourceSlot)
                    .addCallback((v) -> {
                        if (v != null) {
                            removeFromList(v);
                        }
                    });
        }
    }

    protected void removeFromList(U v) {
        grid.group.group = current;
        current.remove(clicked);
        currentSlots.remove(clicked);
        onRemove(v);
        getFilters().initializeForSort(grid.group, __ -> {
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, currentColor);
        getFilters().cloneFrom(savedFilters);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        PGR.blackScreen.renderImpl(sb);
        if (currentDialog != null) {
            currentDialog.render(sb);
        }
        else {
            PGR.helpMeButton.tryRender(sb);
            grid.tryRender(sb);
            cancelButton.tryRender(sb);
            addButton.tryRender(sb);
            openButton.tryRender(sb);
            loadExistingButton.tryRender(sb);
            reloadButton.tryRender(sb);
            contextMenu.tryRender(sb);
            colorButtons.tryRender(sb);
            info.tryRender(sb);
            EUI.sortHeader.render(sb);
            if (!getFilters().isActive) {
                EUI.openFiltersButton.tryRender(sb);
                EUIExporter.exportButton.tryRender(sb);
            }
        }
    }

    @Override
    public void updateImpl() {
        PGR.blackScreen.updateImpl();
        if (currentDialog != null) {
            currentDialog.update();

            if (currentDialog.isDone) {
                currentDialog = null;
                PGR.helpMeButton.setOnClick(() -> EUITourTooltip.queueTutorial(getTour())); // Reset tour that could have been set by pages
            }
        }
        else {
            // Do not close the screen with esc if there is an effect going on
            super.updateImpl();
            contextMenu.tryUpdate();
            boolean shouldDoStandardUpdate = !getFilters().tryUpdate() && !CardCrawlGame.isPopupOpen;
            if (shouldDoStandardUpdate) {
                PGR.helpMeButton.tryUpdate();
                EUI.openFiltersButton.tryUpdate();
                EUIExporter.exportButton.tryUpdate();
                EUI.sortHeader.update();
                info.tryUpdate();
                colorButtons.tryUpdate();
                grid.tryUpdate();
                cancelButton.tryUpdate();
                addButton.tryUpdate();
                openButton.tryUpdate();
                loadExistingButton.tryUpdate();
                reloadButton.tryUpdate();
            }
            EUIExporter.exportDropdown.tryUpdate();
        }
    }

    private void viewDesktopFolderBase() {
        viewDesktopFolder(Gdx.files.local(getFolder()));
    }

    private void viewDesktopFolderForSelected() {
        U sourceSlot = currentSlots.get(clicked);
        if (sourceSlot != null) {
            FileHandle file = sourceSlot.getFileHandle();
            // File should never be a directory
            if (file != null) {
                viewDesktopFolder(file.parent());
            }
        }
    }

    protected abstract GenericFilters<T, V, ?> getFilters();

    protected abstract String getFolder();

    protected abstract EUIItemGrid<T> getGrid();

    protected abstract V getSavedFilters();

    protected abstract PCLCustomEditEntityScreen<U, ?, ?> getScreen(U slot);

    protected abstract Iterable<U> getSlots(AbstractCard.CardColor co);

    protected abstract T makeItem(U slot);

    protected abstract U makeSlot(AbstractCard.CardColor co);

    protected abstract U makeSlot(U other);

    protected abstract U makeSlot(U other, AbstractCard.CardColor co);

    protected abstract void onAdd(U slot);

    protected abstract void onEdit(U slot, String oldID);

    protected abstract void onReload();

    protected abstract void onRemove(U slot);

    public enum ContextOption {
        Duplicate(PGR.core.strings.cedit_duplicate, PCLCustomSelectorScreen::duplicate),
        DuplicateToColor(PGR.core.strings.cedit_duplicateToColor, PCLCustomSelectorScreen::duplicateToColor),
        Delete(PGR.core.strings.cedit_delete, PCLCustomSelectorScreen::remove),
        OpenFolder(PGR.core.strings.cedit_openFolder, PCLCustomSelectorScreen::viewDesktopFolderForSelected);

        public final String name;
        public final ActionT1<PCLCustomSelectorScreen<?, ?, ?>> onSelect;

        ContextOption(String name, ActionT1<PCLCustomSelectorScreen<?, ?, ?>> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}
