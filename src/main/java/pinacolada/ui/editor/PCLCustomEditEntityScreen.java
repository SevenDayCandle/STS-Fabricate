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
import pinacolada.orbs.PCLCustomOrbSlot;
import pinacolada.orbs.PCLOrbData;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLPowerData;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.moves.PMove_StackCustomPower;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.ui.PCLValueEditor;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Collections;

import static extendedui.ui.controls.EUIButton.createHexagonalButton;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_HEIGHT;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_WIDTH;

public abstract class PCLCustomEditEntityScreen<T extends PCLCustomEditorLoadable<U, V>, U extends EditorMaker<V, W>, V extends FabricateItem, W>
        extends PCLEffectWithCallback<Object> {
    public static final float BUTTON_HEIGHT = Settings.HEIGHT * (0.055f);
    public static final float BUTTON_START_X = Settings.WIDTH * (0.35f);
    public static final float BUTTON_WIDTH = Settings.WIDTH * (0.16f);
    public static final float CARD_X = Settings.WIDTH * 0.10f;
    public static final float CARD_Y = Settings.HEIGHT * 0.76f;
    public static final float LABEL_HEIGHT = Settings.HEIGHT * (0.04f);
    public static final float LABEL_WIDTH = Settings.WIDTH * (0.20f);
    public static final float PAGE_SIZE = Settings.HEIGHT * (0.05f);
    public static final float RELIC_Y = Settings.HEIGHT * 0.87f;
    public static final float START_X = Settings.WIDTH * (0.24f);
    public static final float START_Y = Settings.HEIGHT * (0.93f);
    private static ArrayList<AbstractBlight> availableBlights;
    private static ArrayList<AbstractCard> availableCards;
    private static ArrayList<PCLOrbData> availableOrbs;
    private static ArrayList<AbstractPotion> availablePotions;
    private static ArrayList<AbstractRelic> availableRelics;
    private static ArrayList<PCLPowerData> availablePowers;
    public final T currentSlot;
    protected final boolean fromInGame;
    private ArrayList<U> prevBuilders;
    private ArrayList<U> tempBuilders;
    private int selectedPage;
    protected ActionT0 onSave;
    protected EUIContextMenu<ExistingPageOption> existingPageOptions;
    protected EUIContextMenu<NewPageOption> newPageOptions;
    protected PCLCustomGenericPage currentPage;
    public ArrayList<EUIButton> effectPageButtons = new ArrayList<>();
    public ArrayList<EUIButton> powerPageButtons = new ArrayList<>();
    public ArrayList<EUIButton> primaryPageButtons = new ArrayList<>();
    public ArrayList<PCLCustomEffectPage> effectPages = new ArrayList<>();
    private ArrayList<? extends PCLCustomEffectPage> selectedPageList = effectPages;
    public ArrayList<PCLCustomGenericPage> primaryPages = new ArrayList<>();
    public ArrayList<PCLCustomPowerEffectPage> powerPages = new ArrayList<>();
    public EUIButton addPageButton;
    public EUIButton cancelButton;
    public EUIButton imageButton;
    public EUIButton saveButton;
    public EUIButton undoButton;
    public PCLValueEditor upgradeToggle;
    public PCLCustomFormEditor formEditor;
    public PCLEffectWithCallback<?> currentDialog;
    public int upgradeLevel;
    public int currentBuilder;

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

    public static ArrayList<PCLOrbData> getAvailableOrbs() {
        if (PCLCustomEditEntityScreen.availableOrbs == null) {
            PCLCustomEditEntityScreen.availableOrbs = new ArrayList<>(PCLOrbData.getAllData());
            PCLCustomEditEntityScreen.availableOrbs.addAll(EUIUtils.map(PCLCustomOrbSlot.getAll().values(), slot -> slot.getBuilder(0)));
            PCLCustomEditEntityScreen.availableOrbs.sort((a, b) -> StringUtils.compare(a.getName(), b.getName()));
        }
        return PCLCustomEditEntityScreen.availableOrbs;
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
        modifyBuilder(__ -> {
        });
        setCurrentBuilder(tempBuilders.size() - 1);
    }

    protected void addSkillPages() {
        for (PSkill<?> effect : getBuilder().getMoves()) {
            makeEffectPage(effect);
        }
        for (PSkill<?> effect : getBuilder().getPowers()) {
            makePowerPage(effect);
        }
    }

    protected EUIButton createEffectPageButton(PCLCustomEffectPage pg) {
        EUIButton b = new EUIButton(pg.getTextureCache().texture(), new EUIHitbox(0, 0, PAGE_SIZE, PAGE_SIZE))
                .setPosition(BUTTON_START_X + (effectPageButtons.size() * PAGE_SIZE), (PAGE_SIZE * 1.6f))
                .setOnClick(pg, this::openPage)
                .setOnRightClick(() -> {
                    openContextMenuForPage(effectPages, pg);
                })
                .setTooltip(getPageTooltip(pg));
        pg.setButton(b);
        return b;
    }

    protected PCLCustomEffectPage createPageForEffect(PSkill<?> eff) {
        return new PCLCustomEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), eff, PGR.core.strings.cedit_effectX);
    }

    protected EUIButton createPowerPageButton(PCLCustomEffectPage pg) {
        EUIButton b = new EUIButton(pg.getTextureCache().texture(), new EUIHitbox(0, 0, PAGE_SIZE, PAGE_SIZE))
                .setPosition(BUTTON_START_X + (powerPageButtons.size() * PAGE_SIZE), (PAGE_SIZE * 0.6f))
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
        EUITooltip tip = getPageTooltip(pg);
        EUIButton b = new EUIButton(pg.getTextureCache().texture(), new EUIHitbox(0, 0, PAGE_SIZE * 2, PAGE_SIZE * 0.7f))
                .setPosition(BUTTON_START_X + ((i + 0.288f) * 2 * PAGE_SIZE), (PAGE_SIZE * 2.6f))
                .setOnClick(pg, this::openPage)
                .setLabel(EUIFontHelper.buttonFont, 0.6f, tip.title)
                .setTooltip(tip);
        pg.setButton(b);
        return b;
    }

    protected void deletePage(ArrayList<? extends PCLCustomEffectPage> list, int index) {
        PCLCustomEffectPage ef = list.get(index);
        int descIndex = getPageIndex(ef);
        U builder = getBuilder();
        String[] arr = builder.getDescString(builder.getLanguageMap().get(Settings.language));
        if (arr != null && arr.length > descIndex) {
            String[] truncated = new String[arr.length - 1];
            System.arraycopy(arr, 0, truncated, 0, descIndex);
            if (truncated.length > descIndex) {
                System.arraycopy(arr, descIndex + 1, truncated, descIndex, arr.length - descIndex - 1);
            }
            builder.setDescString(builder.getLanguageMap().get(Settings.language), truncated);
        }

        ef = list.remove(index);
        setupPageButtons();
        if (ef == currentPage) {
            PCLCustomGenericPage firstPage = primaryPages.get(0);
            if (firstPage != null) {
                openPage(firstPage);
            }
        }
        updateAllEffects();
        refreshButtons();
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

    public int getPageIndex(PCLCustomEffectPage page) {
        int res = effectPages.indexOf(page);
        if (res > -1) {
            return res;
        }
        res = powerPages.indexOf(page);
        if (res > -1) {
            return effectPages.size() + res;
        }
        return -1;
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), "");
    }

    public int getPowerCount() {
        return powerPages.size();
    }

    public ArrayList<U> getTempBuilders() {
        return tempBuilders;
    }

    protected EUITourTooltip[] getTour() {
        return EUIUtils.array(
                formEditor != null ? new EUITourTooltip(formEditor.header.hb, formEditor.header.tooltip.title, formEditor.header.tooltip.description).setCanDismiss(true) : null,
                imageButton != null ? imageButton.makeTour(true) : null,
                undoButton.makeTour(true),
                saveButton.makeTour(true)
        );
    }

    public void initializeEffectPage(PCLCustomEffectPage page) {
        updateEffects();
        openPage(page);
    }

    public void initializePowerPage(PCLCustomEffectPage page) {
        updateAllEffects();
        openPage(page);
    }

    public PCLCustomEffectPage makeEffectPage(PSkill<?> skill) {
        PCLCustomEffectPage page = createPageForEffect(skill);
        effectPages.add(page);
        page.refresh();
        effectPageButtons.add(createEffectPageButton(page));
        updateAddEffectButton();
        return page;
    }

    protected PCLCustomPowerEffectPage makeNewPowerEffect() {
        PCLCustomPowerEffectPage pEffect = makePowerPage(new PTrigger_When());
        makeEffectPage(new PMove_StackCustomPower(PCLCardTarget.Self, -1, powerPages.indexOf(pEffect)));
        return pEffect;
    }

    protected PCLCustomPowerEffectPage makePowerPage(PSkill<?> skill) {
        PCLCustomPowerEffectPage page = new PCLCustomPowerEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), skill, PGR.core.strings.cedit_powerX);
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
        this.selectedPage = selectedPageList.indexOf(page);
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
                .setPosition(BUTTON_WIDTH * 0.6f, BUTTON_HEIGHT)
                .setColor(Color.FIREBRICK)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, GridCardSelectScreen.TEXT[1])
                .setOnClick(this::end);

        saveButton = createHexagonalButton(0, 0, BUTTON_WIDTH * 1.16f, BUTTON_HEIGHT * 1.31f)
                .setPosition(Settings.WIDTH - BUTTON_WIDTH * 0.85f, BUTTON_HEIGHT)
                .setColor(Color.FOREST)
                .setLabel(EUIFontHelper.buttonFont, 1f, GridCardSelectScreen.TEXT[0])
                .setOnClick(this::save);

        undoButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + LABEL_HEIGHT * 1.8f)
                .setColor(Color.WHITE)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, PGR.core.strings.cedit_undo)
                .setTooltip(PGR.core.strings.cedit_undo, PGR.core.strings.cetut_undo)
                .setOnClick(this::undo);

        addPageButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, PAGE_SIZE * 1.8f, PAGE_SIZE * 0.7f))
                .setColor(Color.FOREST)
                .setLabel(EUIFontHelper.buttonFont, 0.6f, PGR.core.strings.cedit_newEffect)
                .setOnClick(this::openContextMenuForNewEffect)
                .setTooltip(new EUITooltip(PGR.core.strings.cedit_newEffect));

        imageButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(undoButton.hb.cX, undoButton.hb.y + undoButton.hb.height + LABEL_HEIGHT * 0.8f)
                .setColor(Color.WHITE)
                .setTooltip(PGR.core.strings.cedit_loadImage, PGR.core.strings.cetut_primaryImage)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, PGR.core.strings.cedit_loadImage)
                .setOnClick(this::editImage);

        formEditor = new PCLCustomFormEditor(
                new EUIHitbox(Settings.WIDTH * 0.04f, imageButton.hb.y + imageButton.hb.height + LABEL_HEIGHT * 3.2f, Settings.scale * 90f, MENU_HEIGHT), this);

        upgradeToggle = new PCLValueEditor(new EUIHitbox(Settings.WIDTH * 0.08f, formEditor.hb.y + formEditor.hb.height + LABEL_HEIGHT * 1.2f, Settings.scale * 70f, MENU_HEIGHT)
                , SingleCardViewPopup.TEXT[6], this::toggleViewUpgrades)
                .setLimits(0, PSkill.DEFAULT_MAX);
        upgradeToggle.header.hb.setOffsetX(upgradeToggle.hb.width * 0.25f);

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
                        o.onSelect.invoke(this);
                    }
                })
                .setCanAutosizeButton(true)
                .setItems(getNewPageOptions());
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
        modifyBuilder(__ -> {
        });
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
        addPageButton.tryRender(sb);
        PGR.helpMeButton.tryRender(sb);
    }

    protected void save() {
        currentSlot.builders = tempBuilders;
        if (this.onSave != null) {
            this.onSave.invoke();
        }
        end();
    }

    protected void setCurrentBuilder(int index) {
        ArrayList<? extends PCLCustomGenericPage> pageList = effectPages;
        // Attempt to predict the page to open
        int ind = pageList.indexOf(currentPage);
        if (ind < 0) {
            pageList = powerPages;
            ind = pageList.indexOf(currentPage);
            if (ind < 0) {
                pageList = primaryPages;
                ind = Math.max(0, pageList.indexOf(currentPage));
            }
        }

        currentBuilder = MathUtils.clamp(index, 0, tempBuilders.size() - 1);
        setupPages();
        formEditor.refresh();

        PCLCustomGenericPage firstPage = pageList.get(Math.min(ind, pageList.size() - 1));
        if (firstPage != null) {
            openPage(firstPage);
        }
        else {
            currentPage = null;
            refreshButtons();
        }
    }

    public PCLCustomEditEntityScreen<T, U, V, W> setOnSave(ActionT0 onSave) {
        this.onSave = onSave;

        return this;
    }

    protected void setupPageButtons() {
        effectPageButtons.clear();
        powerPageButtons.clear();
        int i = 0;
        for (i = 0; i < effectPages.size(); i++) {
            effectPageButtons.add(createEffectPageButton(effectPages.get(i)));
        }
        updateAddEffectButton();
        for (i = 0; i < powerPages.size(); i++) {
            powerPageButtons.add(createPowerPageButton(powerPages.get(i)));
        }
    }

    protected void setupPages() {
        primaryPages.clear();
        effectPages.clear();
        powerPages.clear();
        primaryPageButtons.clear();
        effectPageButtons.clear();
        powerPageButtons.clear();
        updateAddEffectButton();
        addSkillPages();
        int i = 0;
        for (i = 0; i < primaryPages.size(); i++) {
            primaryPageButtons.add(createPrimaryPageButton(i));
        }
    }

    protected void startTour() {
        EUITourTooltip.queueFirstView(PGR.config.tourEditorForm, getTour());
    }

    protected void swapEffectPages(ArrayList<? extends PCLCustomGenericPage> pages, int dest, int targ) {
        if (dest >= 0 && targ < pages.size()) {
            Collections.swap(pages, dest, targ);
            PCLCustomGenericPage pg = pages.get(dest);
            setupPageButtons();
            refreshButtons();
            updateAllEffects();
        }
    }

    protected void toggleViewUpgrades(int value) {
        upgradeLevel = value;
    }

    protected void undo() {
        if (prevBuilders != null) {
            ArrayList<U> backups = prevBuilders;
            prevBuilders = tempBuilders;
            tempBuilders = backups;
            currentBuilder = MathUtils.clamp(currentBuilder, 0, tempBuilders.size() - 1);
            setupPages();
            formEditor.refresh();
            rebuildItem();
        }
    }

    protected void updateAddEffectButton() {
        addPageButton.setPosition(BUTTON_START_X + ((effectPageButtons.size() + 0.56f) * PAGE_SIZE), (PAGE_SIZE * 1.6f));
    }

    protected void updateAllEffects() {
        modifyBuilder(e -> e.setPSkill(EUIUtils.map(effectPages, p -> p.rootEffect), true, true)
                .setPPower(EUIUtils.map(powerPages, p -> p.rootEffect), true, true));
    }

    protected void updateEffects() {
        modifyBuilder(e -> e.setPSkill(EUIUtils.map(effectPages, p -> p.rootEffect), true, true));
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

    protected void updatePowerEffect() {
        modifyBuilder(e -> e.setPPower(EUIUtils.map(powerPages, p -> p.rootEffect), true, true));
    }

    public void updateUpgradeEditorLimits(int maxUpgradeLevel) {
        updateUpgradeEditorLimits(0, maxUpgradeLevel < 0 ? PSkill.DEFAULT_MAX : maxUpgradeLevel);
    }

    public void updateUpgradeEditorLimits(int min, int max) {
        upgradeToggle.setLimits(min, max).setValue(upgradeLevel, false).setActive(min != max);
    }

    abstract protected void editImage();

    abstract protected void rebuildItem();

    public static class NewPageOption {
        public static NewPageOption Generic = new NewPageOption(PGR.core.strings.cedit_generic, s -> s.initializeEffectPage(s.makeEffectPage(null)));
        public static NewPageOption Power = new NewPageOption(PGR.core.strings.cedit_customPower, s -> s.initializePowerPage(s.makeNewPowerEffect()));

        public final String name;
        public final ActionT1<PCLCustomEditEntityScreen<?, ?, ?, ?>> onSelect;

        public NewPageOption(String name, ActionT1<PCLCustomEditEntityScreen<?, ?, ?, ?>> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }

    public enum ExistingPageOption {
        MoveLeft(PGR.core.strings.cedit_moveLeft, (ls, i, sc) -> sc.swapEffectPages(ls, i - 1, i)),
        MoveRight(PGR.core.strings.cedit_moveRight, (ls, i, sc) -> sc.swapEffectPages(ls, i, i + 1)),
        Delete(PGR.core.strings.cedit_delete, (ls, i, sc) -> sc.deletePage(ls, i));

        public final String name;
        public final ActionT3<ArrayList<? extends PCLCustomEffectPage>, Integer, PCLCustomEditEntityScreen<?, ?, ?, ?>> onSelect;

        ExistingPageOption(String name, ActionT3<ArrayList<? extends PCLCustomEffectPage>, Integer, PCLCustomEditEntityScreen<?, ?, ?, ?>> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}
