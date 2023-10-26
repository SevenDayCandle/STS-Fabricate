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
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCustomCardSlot;
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
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

import static extendedui.ui.controls.EUIButton.createHexagonalButton;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_HEIGHT;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_WIDTH;

public abstract class PCLCustomEditEntityScreen<T extends PCLCustomEditorLoadable<U, V>, U extends EditorMaker<V>, V extends FabricateItem> extends PCLEffectWithCallback<Object> {
    public static final float BUTTON_HEIGHT = Settings.HEIGHT * (0.055f);
    public static final float CARD_X = Settings.WIDTH * 0.10f;
    public static final float CARD_Y = Settings.HEIGHT * 0.76f;
    public static final float START_X = Settings.WIDTH * (0.24f);
    public static final float START_Y = Settings.HEIGHT * (0.93f);
    public static final float LABEL_HEIGHT = Settings.HEIGHT * (0.04f);
    public static final float BUTTON_WIDTH = Settings.WIDTH * (0.16f);
    public static final float LABEL_WIDTH = Settings.WIDTH * (0.20f);
    public static final float BUTTON_CY = BUTTON_HEIGHT * 1.5f;
    public static final float RELIC_Y = Settings.HEIGHT * 0.87f;
    public static final int EFFECT_COUNT = 3;
    private static ArrayList<AbstractRelic> availableRelics;
    private static ArrayList<PCLPowerData> availablePowers;
    private static ArrayList<AbstractPotion> availablePotions;
    private static ArrayList<AbstractCard> availableCards;
    private static ArrayList<AbstractBlight> availableBlights;
    public final T currentSlot;
    public final boolean fromInGame;
    protected ActionT0 onSave;
    public boolean upgraded;
    public PCLCustomFormEditor formEditor;
    public EUIButton imageButton;
    public EUIToggle upgradeToggle;
    public ArrayList<PSkill<?>> currentEffects = new ArrayList<>();
    public ArrayList<PTrigger> currentPowers = new ArrayList<>();
    public ArrayList<EUIButton> pageButtons = new ArrayList<>();
    public ArrayList<PCLCustomGenericPage> pages = new ArrayList<>();
    public ArrayList<PCLCustomEffectPage> effectPages = new ArrayList<>();
    public ArrayList<PCLCustomPowerEffectPage> powerPages = new ArrayList<>();
    public EUIButton cancelButton;
    public EUIButton saveButton;
    public EUIButton undoButton;
    public int currentPage;
    private ArrayList<U> prevBuilders;
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
        openPageAtIndex(0);
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

    protected void clearPages() {
        createCurrentEffects();
        pages.clear();
        effectPages.clear();
        powerPages.clear();
        pageButtons.clear();
    }

    protected void createCurrentEffects() {
        currentEffects.clear();
        currentEffects.addAll(getBuilder().getMoves());
        while (currentEffects.size() < EFFECT_COUNT) {
            currentEffects.add(null);
        }
        currentPowers.clear();
        currentPowers.addAll(getBuilder().getPowers());
        while (currentPowers.size() < getPowerLimit()) {
            currentPowers.add(null);
        }
    }

    protected void end() {
        complete(null);
        // In case the tutorial is still playing
        EUITourTooltip.clearTutorialQueue();
    }

    public U getBuilder() {
        return tempBuilders.get(currentBuilder);
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), "");
    }

    public int getPowerCount() {
        return powerPages.size();
    }

    public int getPowerLimit() {
        return EFFECT_COUNT;
    }

    protected EUITourTooltip[] getTour() {
        return EUIUtils.array(
                formEditor != null ? new EUITourTooltip(formEditor.header.hb, formEditor.header.tooltip.title, formEditor.header.tooltip.description).setCanDismiss(true) : null,
                imageButton != null ? imageButton.makeTour(true) : null,
                undoButton.makeTour(true),
                saveButton.makeTour(true)
        );
    }

    protected void makeEffectPage(int index) {
        PCLCustomEffectPage page = new PCLCustomEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), index
                , EUIUtils.format(PGR.core.strings.cedit_effectX, index + 1), (be) -> updateEffect(index, be));
        pages.add(page);
        effectPages.add(page);
        page.refresh();
    }

    protected void makePowerPage(int index) {
        PCLCustomPowerEffectPage page = new PCLCustomPowerEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), index
                , EUIUtils.format(PGR.core.strings.cedit_powerX, index + 1), (be) -> updatePowerEffect(index, be));
        pages.add(page);
        powerPages.add(page);
        page.refresh();
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

    public void openPageAtIndex(int index) {
        currentPage = index;
        for (int j = 0; j < pageButtons.size(); j++) {
            pageButtons.get(j).setColor(j == index ? Color.WHITE : Color.GRAY);
        }
        pages.get(index).onOpen();
        PGR.helpMeButton.setOnClick(() -> {
                if (EUITourTooltip.isQueueEmpty()) {
                    EUITourTooltip.queueTutorial(pages.get(index).getTour());
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
    }

    public void refreshPages() {
        for (PCLCustomGenericPage b : pages) {
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
        }
    }

    public void renderInnerElements(SpriteBatch sb) {
        cancelButton.tryRender(sb);
        saveButton.tryRender(sb);
        undoButton.tryRender(sb);
        pages.get(currentPage).tryRender(sb);
        for (EUIButton b : pageButtons) {
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
        float offset = Settings.WIDTH * 0.5f - (pages.size() / 2f * BUTTON_HEIGHT);
        for (int i = 0; i < pages.size(); i++) {
            PCLCustomGenericPage pg = pages.get(i);
            pageButtons.add(new EUIButton(pg.getTextureCache().texture(), new EUIHitbox(0, 0, BUTTON_HEIGHT, BUTTON_HEIGHT))
                    .setPosition(offset + (i * BUTTON_HEIGHT), (BUTTON_HEIGHT * 0.85f))
                    .setColor(i == 0 ? Color.WHITE : Color.GRAY)
                    .setOnClick(i, this::openPageAtIndex)
                    .setTooltip(getPageTooltip(pg)));
        }
        modifyBuilder(__ -> {
        });
    }

    protected void setupPages() {
        clearPages();
        addSkillPages();
        setupPageButtons();
    }

    protected void startTour() {
        EUITourTooltip.queueFirstView(PGR.config.tourEditorForm, getTour());
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
            createCurrentEffects();
            updateVariant();
            for (PCLCustomGenericPage b : pages) {
                b.onUndo();
            }
            rebuildItem();
        }
    }

    protected void updateEffect(int index, PSkill<?> be) {
        currentEffects.set(index, be);
        modifyBuilder(e -> e.setPSkill(currentEffects, true, true));
    }

    public void updateInnerElements() {
        cancelButton.tryUpdate();
        saveButton.tryUpdate();
        undoButton.tryUpdate();
        pages.get(currentPage).tryUpdate();
        for (EUIButton b : pageButtons) {
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
        else {
            updateInnerElements();
        }
    }

    protected void updatePowerEffect(int index, PSkill<?> be) {
        if (be instanceof PTrigger) {
            currentPowers.set(index, (PTrigger) be);
        }
        else {
            currentPowers.set(index, null);
        }
        modifyBuilder(e -> e.setPPower(currentPowers, true, true));
    }

    protected void updateVariant() {

    }

    abstract protected void rebuildItem();
}
