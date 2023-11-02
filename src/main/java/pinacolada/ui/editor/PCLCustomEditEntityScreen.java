package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.*;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLDungeon;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLPowerData;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.moves.PMove_StackCustomPower;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Collections;

import static extendedui.ui.controls.EUIButton.createHexagonalButton;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_HEIGHT;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_WIDTH;

public abstract class PCLCustomEditEntityScreen<T extends PCLCustomEditorLoadable<U, V>, U extends EditorMaker<V>, V extends FabricateItem>
        extends PCLEffectWithCallback<Object> {
    public static final float BUTTON_HEIGHT = Settings.HEIGHT * (0.055f);
    public static final float BUTTON_START_X = Settings.WIDTH * (0.3f);
    public static final float CARD_X = Settings.WIDTH * 0.10f;
    public static final float CARD_Y = Settings.HEIGHT * 0.76f;
    public static final float START_X = Settings.WIDTH * (0.24f);
    public static final float START_Y = Settings.HEIGHT * (0.93f);
    public static final float LABEL_HEIGHT = Settings.HEIGHT * (0.04f);
    public static final float BUTTON_WIDTH = Settings.WIDTH * (0.16f);
    public static final float LABEL_WIDTH = Settings.WIDTH * (0.20f);
    public static final float BUTTON_CY = BUTTON_HEIGHT * 1.5f;
    public static final float RELIC_Y = Settings.HEIGHT * 0.87f;
    private static ArrayList<AbstractRelic> availableRelics;
    private static ArrayList<PCLPowerData> availablePowers;
    private static ArrayList<AbstractPotion> availablePotions;
    private static ArrayList<AbstractCard> availableCards;
    private static ArrayList<AbstractBlight> availableBlights;
    public final T currentSlot;
    public final boolean fromInGame;
    private PCLCustomEffectPage selectedPage;
    private ArrayList<U> prevBuilders;
    protected ActionT0 onSave;
    protected EUIContextMenu<ExistingPageOption> existingPageOptions;
    protected EUIContextMenu<NewPageOption> newPageOptions;
    protected PCLCustomGenericPage currentPage;
    public boolean upgraded;
    public PCLCustomFormEditor formEditor;
    public EUIButton imageButton;
    public EUIToggle upgradeToggle;
    public ArrayList<PSkill<?>> currentEffects = new ArrayList<>();
    public ArrayList<PSkill<?>> currentPowers = new ArrayList<>();
    public ArrayList<EUIButton> primaryPageButtons = new ArrayList<>();
    public ArrayList<EUIButton> effectPageButtons = new ArrayList<>();
    public ArrayList<EUIButton> powerPageButtons = new ArrayList<>();
    public ArrayList<PCLCustomGenericPage> primaryPages = new ArrayList<>();
    public ArrayList<PCLCustomEffectPage> effectPages = new ArrayList<>();
    private ArrayList<? extends PCLCustomEffectPage> selectedPageList = effectPages;
    public ArrayList<PCLCustomPowerEffectPage> powerPages = new ArrayList<>();
    public EUIButton addPageButton;
    public EUIButton cancelButton;
    public EUIButton saveButton;
    public EUIButton undoButton;
    public ArrayList<U> tempBuilders;
    public int currentBuilder;
    public PCLEffectWithCallback<?> currentDialog;

    public PCLCustomEditEntityScreen(T slot) {
        this(slot, false);
    }

    public PCLCustomEditEntityScreen(T currentSlot, boolean fromInGame) {
        this.currentSlot = currentSlot;
        this.fromInGame = fromInGame;
        tempBuilders = EUIUtils.map(currentSlot.builders, EditorMaker::makeCopy);

        preInitialize(currentSlot);
        invalidateItems();
        startTour();
        setupPages();
        modifyBuilder(__ -> {
        });

        PCLCustomGenericPage firstPage = primaryPages.get(0);
        if (firstPage != null) {
            openPage(firstPage);
        }
    }

    public static ArrayList<AbstractBlight> getAvailableBlights(AbstractCard.CardColor cardColor) {
        if (PCLCustomEditEntityScreen.availableBlights == null) {
            if (PGR.config.showIrrelevantProperties.get()) {
                PCLCustomEditEntityScreen.availableBlights = EUIGameUtils.getAllBlights();
                PCLCustomEditEntityScreen.availableBlights.addAll(EUIUtils.map(PCLCustomBlightSlot.getBlights(), PCLCustomBlightSlot::make));
            }
            else {
                PCLCustomEditEntityScreen.availableBlights = EUIGameUtils.getAllBlights();
                PCLCustomEditEntityScreen.availableBlights.addAll(EUIUtils.map(PCLCustomBlightSlot.getBlights(cardColor), PCLCustomBlightSlot::make));
                if (cardColor != AbstractCard.CardColor.COLORLESS) {
                    PCLCustomEditEntityScreen.availableBlights.addAll(EUIUtils.map(PCLCustomBlightSlot.getBlights(AbstractCard.CardColor.COLORLESS), PCLCustomBlightSlot::make));
                }
            }
            PCLCustomEditEntityScreen.availableBlights.sort((a, b) -> StringUtils.compare(a.name, b.name));
        }
        return PCLCustomEditEntityScreen.availableBlights;
    }

    public static ArrayList<AbstractCard> getAvailableCards(AbstractCard.CardColor cardColor) {
        if (PCLCustomEditEntityScreen.availableCards == null) {
            if (PGR.config.showIrrelevantProperties.get()) {
                boolean isPCLColor = GameUtilities.isPCLOnlyCardColor(cardColor);
                // Filter template replacements
                PCLCustomEditEntityScreen.availableCards = EUIUtils.filter(CardLibrary.cards.values(), c -> !PGR.core.filterColorless(c));
                PCLCustomEditEntityScreen.availableCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(), PCLCustomCardSlot::make));
            }
            else {
                PCLCustomEditEntityScreen.availableCards = GameUtilities.isPCLOnlyCardColor(cardColor) ? EUIUtils.mapAsNonnull(PCLCardData.getAllData(false, false, cardColor), cd -> cd.makeCardFromLibrary(0)) :
                        EUIUtils.filterInPlace(CardLibrary.getAllCards(),
                                c -> !PCLDungeon.isColorlessCardExclusive(c) && (c.color == AbstractCard.CardColor.COLORLESS || c.color == AbstractCard.CardColor.CURSE || c.color == cardColor));
                PCLCustomEditEntityScreen.availableCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(cardColor), PCLCustomCardSlot::make));
                if (cardColor != AbstractCard.CardColor.COLORLESS) {
                    PCLCustomEditEntityScreen.availableCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS), PCLCustomCardSlot::make));
                }
            }
            PCLCustomEditEntityScreen.availableCards.sort((a, b) -> StringUtils.compare(a.name, b.name));
        }
        return PCLCustomEditEntityScreen.availableCards;
    }

    public static ArrayList<AbstractPotion> getAvailablePotions(AbstractCard.CardColor cardColor) {
        if (PCLCustomEditEntityScreen.availablePotions == null) {
            PCLCustomEditEntityScreen.availablePotions = new ArrayList<>(GameUtilities.getPotions(null));
            PCLCustomEditEntityScreen.availablePotions.sort((a, b) -> StringUtils.compare(a.name, b.name));
        }
        return PCLCustomEditEntityScreen.availablePotions;
    }

    public static ArrayList<PCLPowerData> getAvailablePowers() {
        if (PCLCustomEditEntityScreen.availablePowers == null) {
            PCLCustomEditEntityScreen.availablePowers = new ArrayList<>(PCLPowerData.getAllData());
            PCLCustomEditEntityScreen.availablePowers.addAll(EUIUtils.map(PCLCustomPowerSlot.getAll().values(), slot -> slot.getBuilder(0)));
            PCLCustomEditEntityScreen.availablePowers.sort((a, b) -> StringUtils.compare(a.getName(), b.getName()));
        }
        return PCLCustomEditEntityScreen.availablePowers;
    }

    public static ArrayList<AbstractRelic> getAvailableRelics(AbstractCard.CardColor cardColor) {
        if (PCLCustomEditEntityScreen.availableRelics == null) {
            if (PGR.config.showIrrelevantProperties.get()) {
                PCLCustomEditEntityScreen.availableRelics = EUIGameUtils.getAllRelics();
                PCLCustomEditEntityScreen.availableRelics.addAll(EUIUtils.map(PCLCustomRelicSlot.getRelics(), PCLCustomRelicSlot::make));
            }
            else {
                PCLCustomEditEntityScreen.availableRelics = new ArrayList<>(GameUtilities.getRelics(cardColor).values());
                PCLCustomEditEntityScreen.availableRelics.addAll(EUIUtils.map(PCLCustomRelicSlot.getRelics(cardColor), PCLCustomRelicSlot::make));
                if (cardColor != AbstractCard.CardColor.COLORLESS) {
                    PCLCustomEditEntityScreen.availableRelics.addAll(GameUtilities.getRelics(AbstractCard.CardColor.COLORLESS).values());
                    PCLCustomEditEntityScreen.availableRelics.addAll(EUIUtils.map(PCLCustomRelicSlot.getRelics(AbstractCard.CardColor.COLORLESS), PCLCustomRelicSlot::make));
                }
            }
            PCLCustomEditEntityScreen.availableRelics.sort((a, b) -> StringUtils.compare(a.name, b.name));
        }
        return PCLCustomEditEntityScreen.availableRelics;
    }

    public static void invalidateItems() {
        availableBlights = null;
        availableCards = null;
        availablePotions = null;
        availablePowers = null;
        availableRelics = null;
    }

    public void addBuilder() {
        tempBuilders.add(getBuilder().makeCopy());
        setCurrentBuilder(tempBuilders.size() - 1);
    }

    protected void addSkillPages() {
        for (int i = 0; i < currentEffects.size(); i++) {
            makeEffectPage(i);
        }
        for (int i = 0; i < currentPowers.size(); i++) {
            makePowerPage(i);
        }
    }

    protected void clearButtons() {
        primaryPageButtons.clear();
        effectPageButtons.clear();
        powerPageButtons.clear();
    }

    protected void clearPages() {
        createCurrentEffects();
        primaryPages.clear();
        effectPages.clear();
        powerPages.clear();
        clearButtons();
    }

    protected void createCurrentEffects() {
        currentEffects.clear();
        currentEffects.addAll(getBuilder().getMoves());
        currentPowers.clear();
        currentPowers.addAll(getBuilder().getPowers());
    }

    protected EUIButton createEffectPageButton(PCLCustomEffectPage pg) {
        EUIButton b = new EUIButton(pg.getTextureCache().texture(), new EUIHitbox(0, 0, BUTTON_HEIGHT, BUTTON_HEIGHT))
                .setPosition(BUTTON_START_X + (pg.editorIndex * BUTTON_HEIGHT), (BUTTON_HEIGHT * 1.65f))
                .setOnClick(pg, this::openPage)
                .setOnRightClick(() -> {
                    openContextMenuForPage(effectPages, pg);
                })
                .setTooltip(getPageTooltip(pg));
        pg.setButton(b);
        return b;
    }

    protected EUIButton createPowerPageButton(PCLCustomEffectPage pg) {
        EUIButton b = new EUIButton(pg.getTextureCache().texture(), new EUIHitbox(0, 0, BUTTON_HEIGHT, BUTTON_HEIGHT))
                .setPosition(BUTTON_START_X + (pg.editorIndex * BUTTON_HEIGHT), (BUTTON_HEIGHT * 0.5f))
                .setOnClick(pg, this::openPage)
                .setOnRightClick(() -> {
                    openContextMenuForPage(powerPages, pg);
                })
                .setTooltip(getPageTooltip(pg));
        pg.setButton(b);
        return b;
    }

    protected EUIButton createPrimaryPageButton(int i) {
        PCLCustomGenericPage pg = primaryPages.get(i);
        EUIButton b = new EUIButton(pg.getTextureCache().texture(), new EUIHitbox(0, 0, BUTTON_HEIGHT, BUTTON_HEIGHT))
                .setPosition(BUTTON_START_X + (i * BUTTON_HEIGHT), (BUTTON_HEIGHT * 2.75f))
                .setOnClick(pg, this::openPage)
                .setTooltip(getPageTooltip(pg));
        pg.setButton(b);
        return b;
    }

    protected void deletePage(ArrayList<? extends PCLCustomEffectPage> list, PCLCustomEffectPage page) {
        list.remove(page);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).editorIndex = i;
        }
        clearButtons();
        setupPageButtons();
        if (page == currentPage) {
            PCLCustomGenericPage firstPage = primaryPages.get(0);
            if (firstPage != null) {
                openPage(firstPage);
            }
        }
        modifyBuilder(__ -> {
        });
    }

    protected void end() {
        complete(null);
        // In case the tutorial is still playing
        EUITourTooltip.clearTutorialQueue();
    }

    public U getBuilder() {
        return tempBuilders.get(currentBuilder);
    }

    protected NewPageOption[] getNewPageOptions() {
        return EUIUtils.array(NewPageOption.Generic, NewPageOption.Power);
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), "");
    }

    public int getPowerCount() {
        return powerPages.size();
    }

    protected EUITourTooltip[] getTour() {
        return EUIUtils.array(
                formEditor != null ? new EUITourTooltip(formEditor.header.hb, formEditor.header.tooltip.title, formEditor.header.tooltip.description).setCanDismiss(true) : null,
                imageButton != null ? imageButton.makeTour(true) : null,
                undoButton.makeTour(true),
                saveButton.makeTour(true)
        );
    }

    protected void initializePage(PCLCustomEffectPage page) {
        modifyBuilder(__ -> {});
        openPage(page);
    }

    protected PCLCustomEffectPage makeEffectPage(int index) {
        PCLCustomEffectPage page = new PCLCustomEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), index
                , EUIUtils.format(PGR.core.strings.cedit_effectX, index + 1));
        effectPages.add(page);
        page.refresh();
        effectPageButtons.add(createEffectPageButton(page));
        return page;
    }

    protected PCLCustomEffectPage makeNewBlockEffect() {
        return null;
    }

    protected PCLCustomEffectPage makeNewDamageEffect() {
        return null;
    }

    protected PCLCustomEffectPage makeNewGenericEffect() {
        currentEffects.add(null);
        return makeEffectPage(currentEffects.size() - 1);
    }

    protected PCLCustomPowerEffectPage makeNewPowerEffect() {
        currentPowers.add(new PTrigger_When());
        PCLCustomPowerEffectPage pEffect = makePowerPage(currentPowers.size() - 1);

        currentEffects.add(new PMove_StackCustomPower(PCLCardTarget.Self, -1, pEffect.editorIndex));
        makeEffectPage(currentEffects.size() - 1);
        return pEffect;
    }

    protected PCLCustomPowerEffectPage makePowerPage(int index) {
        PCLCustomPowerEffectPage page = new PCLCustomPowerEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), index
                , EUIUtils.format(PGR.core.strings.cedit_powerX, index + 1));
        powerPages.add(page);
        page.refresh();
        powerPageButtons.add(createPowerPageButton(page));
        return page;
    }

    public void modifyAllBuilders(ActionT2<U, Integer> updateFunc) {
        prevBuilders = EUIUtils.map(tempBuilders, EditorMaker::makeCopy);
        for (int i = 0; i < tempBuilders.size(); i++) {
            updateFunc.invoke(tempBuilders.get(i), i);
        }
        rebuildItem();
    }

    public void modifyBuilder(ActionT1<U> updateFunc) {
        prevBuilders = EUIUtils.map(tempBuilders, EditorMaker::makeCopy);
        updateFunc.invoke(getBuilder());
        rebuildItem();
    }

    protected void openContextMenuForNewEffect() {
        this.newPageOptions.setItems(getNewPageOptions());
        this.newPageOptions.positionToOpen();
    }

    protected void openContextMenuForPage(ArrayList<? extends PCLCustomEffectPage> selectedPageList, PCLCustomEffectPage page) {
        this.selectedPageList = selectedPageList;
        this.selectedPage = page;
        this.existingPageOptions.positionToOpen();
    }

    protected void openPage(PCLCustomGenericPage page) {
        currentPage = page;
        currentPage.onOpen();
        refreshButtons();
        PGR.helpMeButton.setOnClick(() -> {
                    if (EUITourTooltip.isQueueEmpty()) {
                        EUITourTooltip.queueTutorial(currentPage.getTour());
                        EUITourTooltip.queueTutorial(getTour());
                    }
                }
        );
    }

    public void preInitialize(T currentSlot) {
        SingleCardViewPopup.isViewingUpgrade = false; // To avoid glitches while rendering card upgrades, as the renderUpgradePreview function fetches the card's saved forms
        cancelButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(BUTTON_WIDTH * 0.6f, BUTTON_CY)
                .setColor(Color.FIREBRICK)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, GridCardSelectScreen.TEXT[1])
                .setOnClick(this::end);

        saveButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + LABEL_HEIGHT * 0.8f)
                .setColor(Color.FOREST)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, GridCardSelectScreen.TEXT[0])
                .setOnClick(this::save);

        undoButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, saveButton.hb.y + saveButton.hb.height + LABEL_HEIGHT * 0.8f)
                .setColor(Color.WHITE)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, PGR.core.strings.cedit_undo)
                .setTooltip(PGR.core.strings.cedit_undo, PGR.core.strings.cetut_undo)
                .setOnClick(this::undo);

        addPageButton = new EUIButton(EUIRM.images.plus.texture(), new EUIHitbox(0, 0, BUTTON_HEIGHT * 0.8f, BUTTON_HEIGHT * 0.8f))
                .setOnClick(this::openContextMenuForNewEffect)
                .setTooltip(new EUITooltip(PGR.core.strings.cedit_newEffect));

        existingPageOptions = (EUIContextMenu<ExistingPageOption>) new EUIContextMenu<ExistingPageOption>(new EUIHitbox(0, 0, 0, 0), o -> o.name)
                .setOnChange(options -> {
                    for (ExistingPageOption o : options) {
                        o.onSelect.invoke(selectedPageList, selectedPage, this);
                    }
                })
                .setCanAutosizeButton(true)
                .setItems(ExistingPageOption.values());

        newPageOptions = (EUIContextMenu<NewPageOption>) new EUIContextMenu<NewPageOption>(new EUIHitbox(0, 0, 0, 0), o -> o.name)
                .setOnChange(options -> {
                    for (NewPageOption o : options) {
                        initializePage(o.onSelect.invoke(this));
                    }
                })
                .setCanAutosizeButton(true)
                .setItems(NewPageOption.values());
    }

    protected void refreshButtons() {
        for (EUIButton pb : primaryPageButtons) {
            pb.setColor(Color.GRAY);
        }
        for (EUIButton pb : powerPageButtons) {
            pb.setColor(Color.GRAY);
        }
        for (EUIButton pb : effectPageButtons) {
            pb.setColor(Color.GRAY);
        }
        if (currentPage != null) {
            currentPage.highlightButton();
        }
    }

    public void refreshPages() {
        for (PCLCustomGenericPage b : primaryPages) {
            b.refresh();
        }
        for (PCLCustomGenericPage b : effectPages) {
            b.refresh();
        }
        for (PCLCustomGenericPage b : powerPages) {
            b.refresh();
        }
    }

    public void removeBuilder() {
        tempBuilders.remove(getBuilder());
        setCurrentBuilder(currentBuilder);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (currentDialog != null) {
            currentDialog.render(sb);
        }
        else {
            renderInnerElements(sb);
            newPageOptions.tryRender(sb);
            existingPageOptions.tryRender(sb);
        }
    }

    public void renderInnerElements(SpriteBatch sb) {
        cancelButton.tryRender(sb);
        saveButton.tryRender(sb);
        undoButton.tryRender(sb);
        addPageButton.tryRender(sb);
        if (currentPage != null) {
            currentPage.tryRender(sb);
        }
        for (EUIButton b : primaryPageButtons) {
            b.tryRender(sb);
        }
        for (EUIButton b : effectPageButtons) {
            b.tryRender(sb);
        }
        for (EUIButton b : powerPageButtons) {
            b.tryRender(sb);
        }
        PGR.helpMeButton.tryRender(sb);
    }

    protected void save() {
        currentSlot.builders = tempBuilders;
        if (this.onSave != null) {
            this.onSave.invoke();
        }
        end();
    }

    public void setCurrentBuilder(int index) {
        currentBuilder = MathUtils.clamp(index, 0, tempBuilders.size() - 1);
        modifyBuilder(__ -> {
        });
        setupPages();
        updateVariant();
    }

    public PCLCustomEditEntityScreen<T, U, V> setOnSave(ActionT0 onSave) {
        this.onSave = onSave;

        return this;
    }

    protected void setupPageButtons() {
        int i = 0;
        for (i = 0; i < primaryPages.size(); i++) {
            primaryPageButtons.add(createPrimaryPageButton(i));
        }
        addPageButton.setPosition(BUTTON_START_X + (i * BUTTON_HEIGHT), (BUTTON_HEIGHT * 2.75f));
        for (i = 0; i < effectPages.size(); i++) {
            effectPageButtons.add(createEffectPageButton(effectPages.get(i)));
        }
        for (i = 0; i < powerPages.size(); i++) {
            powerPageButtons.add(createPowerPageButton(powerPages.get(i)));
        }
    }

    protected void setupPages() {
        clearPages();
        addSkillPages();
        setupPageButtons();
    }

    protected void startTour() {
        EUITourTooltip.queueFirstView(PGR.config.tourEditorForm, getTour());
    }

    protected void swapEffectPages(ArrayList<? extends PCLCustomGenericPage> pages, int dest, int targ) {
        if (dest >= 0 && targ < pages.size()) {
            Collections.swap(pages, dest, targ);
            PCLCustomGenericPage pg = pages.get(dest);
            if (pg instanceof PCLCustomEffectPage) {
                ((PCLCustomEffectPage) pg).editorIndex = dest;
            }
            PCLCustomGenericPage pg2 = pages.get(targ);
            if (pg2 instanceof PCLCustomEffectPage) {
                ((PCLCustomEffectPage) pg2).editorIndex = targ;
            }
            clearButtons();
            setupPageButtons();
            refreshButtons();
            modifyBuilder(__ -> {
            });
        }
    }

    protected void toggleViewUpgrades(boolean value) {
        upgraded = value;
    }

    protected void undo() {
        if (prevBuilders != null) {
            ArrayList<U> backups = prevBuilders;
            prevBuilders = tempBuilders;
            tempBuilders = backups;
            currentBuilder = MathUtils.clamp(currentBuilder, 0, tempBuilders.size() - 1);
            setupPages();
            updateVariant();
            rebuildItem();
        }
    }

    protected void updateEffect(PSkill<?> be, int index) {
        currentEffects.set(index, be);
        modifyBuilder(e -> e.setPSkill(currentEffects, true, true));
    }

    public void updateInnerElements() {
        cancelButton.tryUpdate();
        saveButton.tryUpdate();
        undoButton.tryUpdate();
        addPageButton.tryUpdate();
        if (currentPage != null) {
            currentPage.tryUpdate();
        }
        for (EUIButton b : primaryPageButtons) {
            b.tryUpdate();
        }
        for (EUIButton b : effectPageButtons) {
            b.tryUpdate();
        }
        for (EUIButton b : powerPageButtons) {
            b.tryUpdate();
        }
        PGR.helpMeButton.tryUpdate();
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (currentDialog != null) {
            currentDialog.update();
            if (currentDialog.isDone) {
                currentDialog = null;
            }
        }
        else if (!newPageOptions.isOpen() && !existingPageOptions.isOpen()) {
            updateInnerElements();
        }
        newPageOptions.tryUpdate();
        existingPageOptions.tryUpdate();
    }

    protected void updatePowerEffect(PSkill<?> be, int index) {
        currentPowers.set(index, be);
        modifyBuilder(e -> e.setPPower(currentPowers, true, true));
    }

    protected void updateVariant() {

    }

    abstract protected void rebuildItem();

    public enum ExistingPageOption {
        MoveLeft(PGR.core.strings.cedit_moveLeft, (ls, pg, sc) -> sc.swapEffectPages(ls, pg.editorIndex - 1, pg.editorIndex)),
        MoveRight(PGR.core.strings.cedit_moveRight, (ls, pg, sc) -> sc.swapEffectPages(ls, pg.editorIndex, pg.editorIndex + 1)),
        Delete(PGR.core.strings.cedit_delete, (ls, pg, sc) -> sc.deletePage(ls, pg));

        public final String name;
        public final ActionT3<ArrayList<? extends PCLCustomEffectPage>, PCLCustomEffectPage, PCLCustomEditEntityScreen<?, ?, ?>> onSelect;

        ExistingPageOption(String name, ActionT3<ArrayList<? extends PCLCustomEffectPage>, PCLCustomEffectPage, PCLCustomEditEntityScreen<?, ?, ?>> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }

    public enum NewPageOption {
        Generic(PGR.core.strings.cedit_generic, PCLCustomEditEntityScreen::makeNewGenericEffect),
        Power(PGR.core.strings.cedit_customPower, PCLCustomEditEntityScreen::makeNewPowerEffect),
        Damage(PGR.core.strings.cedit_damage, PCLCustomEditEntityScreen::makeNewDamageEffect),
        Block(PGR.core.strings.cedit_block, PCLCustomEditEntityScreen::makeNewBlockEffect);

        public final String name;
        public final FuncT1<PCLCustomEffectPage, PCLCustomEditEntityScreen<?, ?, ?>> onSelect;

        NewPageOption(String name, FuncT1<PCLCustomEffectPage, PCLCustomEditEntityScreen<?, ?, ?>> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}
