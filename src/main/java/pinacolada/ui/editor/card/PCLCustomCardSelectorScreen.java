package pinacolada.ui.editor.card;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.TemplateCardData;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.PCLCustomCardCopyConfirmationEffect;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.effects.screen.PCLGenericSelectCardEffect;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PCLCustomCardSelectorScreen extends AbstractMenuScreen {
    private static final float DRAW_START_X = (Settings.WIDTH - (5f * AbstractCard.IMG_WIDTH * 0.75f) - (4f * Settings.CARD_VIEW_PAD_X) + AbstractCard.IMG_WIDTH * 0.75f);
    private static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    private static final float PAD_Y = AbstractCard.IMG_HEIGHT * 0.75f + Settings.CARD_VIEW_PAD_Y;
    private static final float SCROLL_BAR_THRESHOLD = 500f * Settings.scale;
    protected static final float ITEM_HEIGHT = AbstractCard.IMG_HEIGHT * 0.15f;
    public static AbstractCard.CardColor currentColor = AbstractCard.CardColor.COLORLESS;
    public static AbstractPlayer.PlayerClass currentClass;
    protected final EUICardGrid grid;
    protected final EUIToggle toggle;
    private AbstractCard clickedCard;
    protected ActionT0 onClose;
    protected EUIButton addButton;
    protected EUIButton cancelButton;
    protected EUIButton loadExistingButton;
    protected EUIButton openButton;
    protected EUIButton reloadButton;
    protected EUIButtonList colorButtons;
    protected EUIContextMenu<ContextOption> contextMenu;
    protected EUITextBox info;
    protected HashMap<AbstractCard, PCLCustomCardSlot> currentSlots = new HashMap<>();
    protected PCLEffectWithCallback<?> currentDialog;

    public PCLCustomCardSelectorScreen() {
        final float buttonHeight = screenH(0.06f);
        final float labelHeight = screenH(0.04f);
        final float buttonWidth = screenW(0.18f);
        final float labelWidth = screenW(0.20f);

        this.grid = new EUICardGrid(1f)
                .setEnlargeOnHover(false)
                .setOnCardClick(this::onCardClicked)
                .setOnCardRightClick(this::onCardRightClicked);
        toggle = new EUIToggle(new EUIHitbox(0, 0, AbstractCard.IMG_WIDTH * 0.2f, ITEM_HEIGHT))
                .setBackground(EUIRM.images.greySquare.texture(), com.badlogic.gdx.graphics.Color.DARK_GRAY)
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

        openButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(addButton.hb.cX, addButton.hb.y + addButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_openFolder)
                .setTooltip(PGR.core.strings.cedit_openFolder, PGR.core.strings.cetut_openFolder)
                .setOnClick(PCLCustomCardSelectorScreen::openFolder);

        loadExistingButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(openButton.hb.cX, openButton.hb.y + openButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_loadFromCard)
                .setTooltip(PGR.core.strings.cedit_loadFromCard, PGR.core.strings.cetut_loadFromCardScreen)
                .setOnClick(this::loadFromExisting);

        reloadButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(loadExistingButton.hb.cX, loadExistingButton.hb.y + loadExistingButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_reloadCards)
                .setTooltip(PGR.core.strings.cedit_reloadCards, PGR.core.strings.cetut_selectorReload)
                .setOnClick(PCLCustomCardSlot::initialize);

        contextMenu = (EUIContextMenu<ContextOption>) new EUIContextMenu<ContextOption>(new EUIHitbox(-500f, -500f, 0, 0), o -> o.name)
                .setItems(ContextOption.values())
                .setOnChange(options -> {
                    for (ContextOption o : options) {
                        o.onSelect.invoke(this, clickedCard, currentSlots.get(clickedCard));
                    }
                })
                .setCanAutosizeButton(true);
        info = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(screenW(0.25f), screenH(0.035f), screenW(0.5f), buttonHeight * 2f))
                .setLabel(PGR.core.strings.cetut_selector1)
                .setAlignment(0.75f, 0.05f, true)
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 0.9f);

        colorButtons = new EUIButtonList(14, screenW(0.09f), screenH(0.95f), EUIButtonList.BUTTON_W, EUIButtonList.BUTTON_H);

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
            PCLCustomCardSlot slot = new PCLCustomCardSlot(currentColor);
            currentDialog = new PCLCustomCardEditCardScreen(slot)
                    .setOnSave(() -> {
                        AbstractCard newCard = slot.make(false);
                        currentSlots.put(newCard, slot);
                        PCLCustomCardSlot.getCards(currentColor).add(slot);
                        grid.addCard(newCard);
                        slot.commitBuilder();
                    });
        }
    }

    public void duplicate(AbstractCard card, PCLCustomCardSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            PCLCustomCardSlot slot = new PCLCustomCardSlot(cardSlot);
            currentDialog = new PCLCustomCardEditCardScreen(slot)
                    .setOnSave(() -> {
                        slot.commitBuilder();
                        AbstractCard newCard = slot.getBuilder(0).createImplWithForms(0, 0, false);
                        currentSlots.put(newCard, slot);
                        PCLCustomCardSlot.getCards(currentColor).add(slot);
                        grid.addCard(newCard);
                    });
        }
    }

    public void duplicateToColor(AbstractCard card, PCLCustomCardSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            currentDialog = new PCLCustomCardCopyConfirmationEffect(getAllColors())
                    .addCallback((co) -> {
                        if (co != null) {
                            PCLCustomCardSlot slot = new PCLCustomCardSlot(cardSlot, co);
                            open(null, co, this.onClose);
                            currentDialog = new PCLCustomCardEditCardScreen(slot)
                                    .setOnSave(() -> {
                                        slot.commitBuilder();
                                        AbstractCard newCard = slot.getBuilder(0).createImplWithForms(0, 0, false);
                                        currentSlots.put(newCard, slot);
                                        PCLCustomCardSlot.getCards(co).add(slot);
                                        grid.addCard(newCard);
                                    });
                        }
                    });
        }
    }

    public void edit(AbstractCard card, PCLCustomCardSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            currentDialog = new PCLCustomCardEditCardScreen(cardSlot)
                    .setOnSave(() -> {
                        cardSlot.commitBuilder();
                        AbstractCard newCard = cardSlot.getBuilder(0).createImplWithForms(0, 0, false);
                        grid.removeCard(card);
                        currentSlots.remove(card);
                        currentSlots.put(newCard, cardSlot);
                        grid.addCard(newCard);
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
    private ArrayList<AbstractCard> getAvailableCardsToCopy() {
        ArrayList<AbstractCard> cards = EUIUtils.mapAsNonnull(TemplateCardData.getTemplates(),
                data -> {
                    PCLCard card = data.create();
                    UnlockTracker.markCardAsSeen(data.ID);
                    card.isSeen = true;
                    card.isLocked = false;
                    // Hide the affinities for colorless cards
                    if (!PGR.config.showIrrelevantProperties.get() && GameUtilities.isColorlessCardColor(currentColor)) {
                        card.affinities.sorted.clear();
                    }
                    return PCLCustomCardSlot.canFullyCopyCard(card) ? card : null;
                });
        cards.sort((a, b) -> StringUtils.compare(a.name, b.name));
        return cards;
    }

    public void loadFromExisting() {
        if (currentDialog == null) {
            currentDialog = new PCLGenericSelectCardEffect(this.getAvailableCardsToCopy()).addCallback(card -> {
                if (card instanceof PCLCard) {
                    PCLCustomCardSlot slot = new PCLCustomCardSlot((PCLCard) card, currentColor);
                    currentDialog = new PCLCustomCardEditCardScreen(slot)
                            .setOnSave(() -> {
                                slot.commitBuilder();
                                AbstractCard newCard = slot.make(false);
                                currentSlots.put(newCard, slot);
                                PCLCustomCardSlot.getCards(currentColor).add(slot);
                                grid.addCard(newCard);
                            });
                }
            });

        }
    }

    private void makeColorButton(AbstractCard.CardColor co) {
        colorButtons.addButton(button -> open(null, co, this.onClose), EUIGameUtils.getColorName(co))
                .setColor(EUIGameUtils.getColorColor(co));
    }

    private void onCardClicked(AbstractCard card) {
        PCLCustomCardSlot slot = currentSlots.get(card);
        if (slot != null) {
            edit(card, slot);
        }
    }

    private void onCardRightClicked(AbstractCard card) {
        clickedCard = card;
        contextMenu.positionToOpen();
    }

    public void open(AbstractPlayer.PlayerClass playerClass, AbstractCard.CardColor cardColor, ActionT0 onClose) {
        super.open();

        currentClass = playerClass;
        currentColor = EUI.actingColor = cardColor;
        currentSlots.clear();
        grid.clear();
        for (PCLCustomCardSlot slot : PCLCustomCardSlot.getCards(currentColor)) {
            AbstractCard card = slot.make();
            currentSlots.put(card, slot);
            grid.addCard(card);
        }
        EUI.cardFilters.initializeForCustomHeader(grid.cards, __ -> {
            grid.moveToTop();
            grid.forceUpdateCardPositions();
        }, currentColor, false, true);

        EUITourTooltip.queueFirstView(PGR.config.tourItemScreen,
                addButton.makeTour(true),
                openButton.makeTour(true),
                loadExistingButton.makeTour(true),
                reloadButton.makeTour(true));
    }

    public void remove(AbstractCard card, PCLCustomCardSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            currentDialog = new PCLCustomDeletionConfirmationEffect<PCLCustomCardSlot>(cardSlot)
                    .addCallback((v) -> {
                        if (v != null) {
                            grid.removeCard(card);
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
            grid.tryRender(sb);
            EUI.customHeader.render(sb);
            info.tryRender(sb);
            cancelButton.tryRender(sb);
            addButton.tryRender(sb);
            openButton.tryRender(sb);
            loadExistingButton.tryRender(sb);
            reloadButton.tryRender(sb);
            contextMenu.tryRender(sb);
            colorButtons.tryRender(sb);
            if (!EUI.cardFilters.isActive) {
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
            }
        }
        else {
            // Do not close the screen with esc if there is an effect going on
            super.updateImpl();
            contextMenu.tryUpdate();
            boolean shouldDoStandardUpdate = !EUI.cardFilters.tryUpdate() && !CardCrawlGame.isPopupOpen;
            if (shouldDoStandardUpdate) {
                EUI.openFiltersButton.tryUpdate();
                EUIExporter.exportButton.tryUpdate();
                info.tryUpdate();
                colorButtons.tryUpdate();
                EUI.customHeader.update();
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

    public enum ContextOption {
        Duplicate(PGR.core.strings.cedit_duplicate, PCLCustomCardSelectorScreen::duplicate),
        DuplicateToColor(PGR.core.strings.cedit_duplicateToColor, PCLCustomCardSelectorScreen::duplicateToColor),
        Delete(PGR.core.strings.cedit_delete, PCLCustomCardSelectorScreen::remove);

        public final String name;
        public final ActionT3<PCLCustomCardSelectorScreen, AbstractCard, PCLCustomCardSlot> onSelect;

        ContextOption(String name, ActionT3<PCLCustomCardSelectorScreen, AbstractCard, PCLCustomCardSlot> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}
