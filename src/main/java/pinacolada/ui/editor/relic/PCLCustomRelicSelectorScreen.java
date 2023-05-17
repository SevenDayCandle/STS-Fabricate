package pinacolada.ui.editor.relic;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.PCLCustomCardCopyConfirmationEffect;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.effects.screen.PCLGenericSelectRelicEffect;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLPointerRelic;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.TemplateRelicData;
import pinacolada.resources.PGR;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PCLCustomRelicSelectorScreen extends AbstractMenuScreen {
    private static final float DRAW_START_X = (Settings.WIDTH - (5f * AbstractCard.IMG_WIDTH * 0.75f) - (4f * Settings.CARD_VIEW_PAD_X) + AbstractCard.IMG_WIDTH * 0.75f);
    private static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    private static final float PAD_Y = AbstractCard.IMG_HEIGHT * 0.75f + Settings.CARD_VIEW_PAD_Y;
    private static final float SCROLL_BAR_THRESHOLD = 500f * Settings.scale;
    protected static final float ITEM_HEIGHT = AbstractCard.IMG_HEIGHT * 0.15f;
    public static AbstractCard.CardColor currentColor = AbstractCard.CardColor.COLORLESS;
    public static AbstractPlayer.PlayerClass currentClass;
    protected final EUIStaticRelicGrid grid;
    protected final EUIToggle toggle;
    private AbstractRelic clickedRelic;
    protected ActionT0 onClose;
    protected EUIButton addButton;
    protected EUIButton cancelButton;
    protected EUIButton loadExistingButton;
    protected EUIButton openButton;
    protected EUIButton reloadButton;
    protected EUIButtonList colorButtons;
    protected EUIContextMenu<ContextOption> contextMenu;
    protected EUITextBox info;
    protected HashMap<AbstractRelic, PCLCustomRelicSlot> currentSlots = new HashMap<>();
    protected PCLEffectWithCallback<?> currentDialog;

    public PCLCustomRelicSelectorScreen() {
        final float buttonHeight = screenH(0.06f);
        final float labelHeight = screenH(0.04f);
        final float buttonWidth = screenW(0.18f);
        final float labelWidth = screenW(0.20f);

        this.grid = (EUIStaticRelicGrid) new EUIStaticRelicGrid(1f)
                .setOnRelicClick(this::onRelicClicked)
                .setOnRelicRightClick(this::onRelicRightClicked);
        toggle = new EUIToggle(new EUIHitbox(0, 0, AbstractCard.IMG_WIDTH * 0.2f, ITEM_HEIGHT))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.65f)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f);
        cancelButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(buttonWidth * 0.53f, buttonHeight)
                .setColor(Color.FIREBRICK)
                .setText(GridCardSelectScreen.TEXT[1])
                .setOnClick(AbstractDungeon::closeCurrentScreen);

        addButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.FOREST)
                .setText(PGR.core.strings.cedit_newCard)
                .setOnClick(this::add);

        openButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(addButton.hb.cX, addButton.hb.y + addButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_openFolder)
                .setOnClick(PCLCustomRelicSelectorScreen::openFolder);

        loadExistingButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(openButton.hb.cX, openButton.hb.y + openButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_loadFromCard)
                .setTooltip(PGR.core.strings.cedit_loadFromCard, PGR.core.strings.cetut_loadFromCardScreen)
                .setOnClick(this::loadFromExisting);

        reloadButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(loadExistingButton.hb.cX, loadExistingButton.hb.y + loadExistingButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_reloadCards)
                .setTooltip(PGR.core.strings.cedit_reloadCards, PGR.core.strings.cetut_selectorReload)
                .setOnClick(PCLCustomRelicSlot::initialize);

        contextMenu = (EUIContextMenu<ContextOption>) new EUIContextMenu<ContextOption>(new EUIHitbox(-500f, -500f, 0, 0), o -> o.name)
                .setItems(ContextOption.values())
                .setOnChange(options -> {
                    for (ContextOption o : options) {
                        o.onSelect.invoke(this, clickedRelic, currentSlots.get(clickedRelic));
                    }
                })
                .setCanAutosizeButton(true);
        info = new EUITextBox(EUIRM.images.panel.texture(), new EUIHitbox(screenW(0.25f), screenH(0.035f), screenW(0.5f), buttonHeight * 2.5f))
                .setLabel(EUIUtils.joinStrings(EUIUtils.DOUBLE_SPLIT_LINE, PGR.core.strings.cetut_selector1, PGR.core.strings.cetut_selector2))
                .setAlignment(0.75f, 0.1f, true)
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 0.9f);

        colorButtons = new EUIButtonList(14, screenW(0.09f), screenH(0.95f), EUIButtonList.BUTTON_W, scale(47));

        for (AbstractCard.CardColor color : getAllColors()) {
            makeColorButton(color);
        }
    }

    public static void openFolder() {
        try {
            Desktop.getDesktop().open(Gdx.files.local(PCLCustomLoadable.FOLDER).file());
        }
        catch (Exception e) {
            EUIUtils.logError(null, "Failed to open card folder.");
        }
    }

    public void add() {
        if (currentDialog == null) {
            PCLCustomRelicSlot slot = new PCLCustomRelicSlot(currentColor);
            currentDialog = new PCLCustomRelicEditRelicScreen(slot)
                    .setOnSave(() -> {
                        AbstractRelic newRelic = slot.makeRelic();
                        currentSlots.put(newRelic, slot);
                        PCLCustomRelicSlot.getRelics(currentColor).add(slot);
                        grid.addRelic(newRelic);
                        slot.commitBuilder();
                    });
        }
    }

    public void duplicate(AbstractRelic card, PCLCustomRelicSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            PCLCustomRelicSlot slot = new PCLCustomRelicSlot(cardSlot);
            currentDialog = new PCLCustomRelicEditRelicScreen(slot)
                    .setOnSave(() -> {
                        slot.commitBuilder();
                        AbstractRelic newRelic = slot.getBuilder(0).create();
                        currentSlots.put(newRelic, slot);
                        PCLCustomRelicSlot.getRelics(currentColor).add(slot);
                        grid.addRelic(newRelic);
                    });
        }
    }

    public void duplicateToColor(AbstractRelic card, PCLCustomRelicSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            currentDialog = new PCLCustomCardCopyConfirmationEffect(getAllColors())
                    .addCallback((co) -> {
                        if (co != null) {
                            PCLCustomRelicSlot slot = new PCLCustomRelicSlot(cardSlot, co);
                            open(null, co, this.onClose);
                            currentDialog = new PCLCustomRelicEditRelicScreen(slot)
                                    .setOnSave(() -> {
                                        slot.commitBuilder();
                                        AbstractRelic newRelic = slot.getBuilder(0).create();
                                        currentSlots.put(newRelic, slot);
                                        PCLCustomRelicSlot.getRelics(co).add(slot);
                                        grid.addRelic(newRelic);
                                    });
                        }
                    });
        }
    }

    public void edit(AbstractRelic card, PCLCustomRelicSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            currentDialog = new PCLCustomRelicEditRelicScreen(cardSlot)
                    .setOnSave(() -> {
                        cardSlot.commitBuilder();
                        AbstractRelic newRelic = cardSlot.getBuilder(0).create();
                        grid.removeRelic(card);
                        currentSlots.remove(card);
                        currentSlots.put(newRelic, cardSlot);
                        grid.addRelic(newRelic);
                    });
        }
    }

    private ArrayList<AbstractCard.CardColor> getAllColors() {
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

    // TODO add replacement cards
    private ArrayList<AbstractRelic> getAvailableRelicsToCopy() {
        return EUIUtils.mapAsNonnull(TemplateRelicData.getTemplates(),
                data -> {
                    PCLRelic relic = data.create();
                    UnlockTracker.markRelicAsSeen(data.ID);
                    relic.isSeen = true;
                    return PCLCustomRelicSlot.canFullyRelicCard(relic) ? relic : null;
                });
    }

    public void loadFromExisting() {
        if (currentDialog == null) {
            currentDialog = new PCLGenericSelectRelicEffect(this.getAvailableRelicsToCopy()).addCallback(card -> {
                if (card instanceof PCLPointerRelic) {
                    PCLCustomRelicSlot slot = new PCLCustomRelicSlot((PCLPointerRelic) card, currentColor);
                    currentDialog = new PCLCustomRelicEditRelicScreen(slot)
                            .setOnSave(() -> {
                                slot.commitBuilder();
                                AbstractRelic newRelic = slot.makeRelic();
                                currentSlots.put(newRelic, slot);
                                PCLCustomRelicSlot.getRelics(currentColor).add(slot);
                                grid.addRelic(newRelic);
                            });
                }
            });

        }
    }

    private void makeColorButton(AbstractCard.CardColor co) {
        colorButtons.addButton(button -> open(null, co, this.onClose), EUIGameUtils.getColorName(co))
                .setColor(EUIGameUtils.getColorColor(co));
    }

    private void onRelicClicked(AbstractRelic card) {
        PCLCustomRelicSlot slot = currentSlots.get(card);
        if (slot != null) {
            edit(card, slot);
        }
    }

    private void onRelicRightClicked(AbstractRelic card) {
        clickedRelic = card;
        contextMenu.positionToOpen();
    }

    public void open(AbstractPlayer.PlayerClass playerClass, AbstractCard.CardColor cardColor, ActionT0 onClose) {
        super.open();

        currentClass = playerClass;
        currentColor = EUI.actingColor = cardColor;
        currentSlots.clear();
        grid.clear();
        for (PCLCustomRelicSlot slot : PCLCustomRelicSlot.getRelics(currentColor)) {
            AbstractRelic relic = slot.getBuilder(0).create();
            currentSlots.put(relic, slot);
            grid.addRelic(relic);
        }
        EUI.relicFilters.initializeForCustomHeader(grid.relicGroup, __ -> {
            grid.moveToTop();
            grid.forceUpdateRelicPositions();
        }, currentColor, false, true);
    }

    public void remove(AbstractRelic card, PCLCustomRelicSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            currentDialog = new PCLCustomDeletionConfirmationEffect<PCLCustomRelicSlot>(cardSlot)
                    .addCallback((v) -> {
                        if (v != null) {
                            grid.removeRelic(card);
                            currentSlots.remove(card);
                            v.wipeBuilder();
                        }
                    });
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        PGR.blackScreen.renderImpl(sb);
        if (currentDialog != null) {
            currentDialog.render(sb);
        }
        else {
            info.tryRender(sb);
            grid.tryRender(sb);
            cancelButton.tryRender(sb);
            addButton.tryRender(sb);
            openButton.tryRender(sb);
            loadExistingButton.tryRender(sb);
            reloadButton.tryRender(sb);
            contextMenu.tryRender(sb);
            colorButtons.tryRender(sb);
            if (!EUI.cardFilters.isActive) {
                EUI.openCardFiltersButton.tryRender(sb);
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
            }
        }
        else {
            // Do not close the screen with esc if there is an effect going on
            super.updateImpl();
            contextMenu.tryUpdate();
            boolean shouldDoStandardUpdate = !EUI.cardFilters.tryUpdate() && !CardCrawlGame.isPopupOpen;
            if (shouldDoStandardUpdate) {
                EUI.openCardFiltersButton.tryUpdate();
                info.tryUpdate();
                colorButtons.tryUpdate();
                grid.tryUpdate();
                cancelButton.tryUpdate();
                addButton.tryUpdate();
                openButton.tryUpdate();
                loadExistingButton.tryUpdate();
                reloadButton.tryUpdate();
            }
        }
    }

    public enum ContextOption {
        Duplicate(PGR.core.strings.cedit_duplicate, PCLCustomRelicSelectorScreen::duplicate),
        DuplicateToColor(PGR.core.strings.cedit_duplicateToColor, PCLCustomRelicSelectorScreen::duplicateToColor),
        Delete(PGR.core.strings.cedit_delete, PCLCustomRelicSelectorScreen::remove);

        public final String name;
        public final ActionT3<PCLCustomRelicSelectorScreen, AbstractRelic, PCLCustomRelicSlot> onSelect;

        ContextOption(String name, ActionT3<PCLCustomRelicSelectorScreen, AbstractRelic, PCLCustomRelicSlot> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}
