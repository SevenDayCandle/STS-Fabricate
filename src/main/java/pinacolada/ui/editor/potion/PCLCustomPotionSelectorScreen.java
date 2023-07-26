package pinacolada.ui.editor.potion;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
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
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.PotionInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.PCLCustomCardCopyConfirmationEffect;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.effects.screen.PCLGenericSelectPotionEffect;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.potions.PCLPotion;
import pinacolada.potions.PCLPotionData;
import pinacolada.resources.PGR;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PCLCustomPotionSelectorScreen extends AbstractMenuScreen {
    private static final float DRAW_START_X = (Settings.WIDTH - (5f * AbstractCard.IMG_WIDTH * 0.75f) - (4f * Settings.CARD_VIEW_PAD_X) + AbstractCard.IMG_WIDTH * 0.75f);
    private static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    private static final float PAD_Y = AbstractCard.IMG_HEIGHT * 0.75f + Settings.CARD_VIEW_PAD_Y;
    private static final float SCROLL_BAR_THRESHOLD = 500f * Settings.scale;
    protected static final float ITEM_HEIGHT = AbstractCard.IMG_HEIGHT * 0.15f;
    public static AbstractCard.CardColor currentColor = AbstractCard.CardColor.COLORLESS;
    public static AbstractPlayer.PlayerClass currentClass;
    protected final EUIPotionGrid grid;
    protected final EUIToggle toggle;
    private AbstractPotion clickedPotion;
    protected ActionT0 onClose;
    protected EUIButton addButton;
    protected EUIButton cancelButton;
    protected EUIButton loadExistingButton;
    protected EUIButton openButton;
    protected EUIButton reloadButton;
    protected EUIButtonList colorButtons;
    protected EUIContextMenu<ContextOption> contextMenu;
    protected EUITextBox info;
    protected HashMap<AbstractPotion, PCLCustomPotionSlot> currentSlots = new HashMap<>();
    protected PCLEffectWithCallback<?> currentDialog;

    public PCLCustomPotionSelectorScreen() {
        final float buttonHeight = screenH(0.06f);
        final float labelHeight = screenH(0.04f);
        final float buttonWidth = screenW(0.18f);
        final float labelWidth = screenW(0.20f);

        this.grid = (EUIPotionGrid) new EUIPotionGrid(1f)
                .setOnClick(this::onPotionClicked)
                .setOnRightClick(this::onPotionRightClicked);
        toggle = new EUIToggle(new EUIHitbox(0, 0, AbstractCard.IMG_WIDTH * 0.2f, ITEM_HEIGHT))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
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
                .setOnClick(PCLCustomPotionSelectorScreen::openFolder);

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
                .setOnClick(PCLCustomPotionSlot::initialize);

        contextMenu = (EUIContextMenu<ContextOption>) new EUIContextMenu<ContextOption>(new EUIHitbox(-500f, -500f, 0, 0), o -> o.name)
                .setItems(ContextOption.values())
                .setOnChange(options -> {
                    for (ContextOption o : options) {
                        o.onSelect.invoke(this, clickedPotion, currentSlots.get(clickedPotion));
                    }
                })
                .setCanAutosizeButton(true);
        info = new EUITextBox(EUIRM.images.panel.texture(), new EUIHitbox(screenW(0.25f), screenH(0.035f), screenW(0.5f), buttonHeight * 2.5f))
                .setLabel(PGR.core.strings.cetut_selector2)
                .setAlignment(0.75f, 0.1f, true)
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
            PCLCustomPotionSlot slot = new PCLCustomPotionSlot(currentColor);
            currentDialog = new PCLCustomPotionEditPotionScreen(slot)
                    .setOnSave(() -> {
                        AbstractPotion newPotion = slot.make();
                        currentSlots.put(newPotion, slot);
                        PCLCustomPotionSlot.getPotions(currentColor).add(slot);
                        grid.add(newPotion);
                        slot.commitBuilder();
                    });
        }
    }

    public void duplicate(AbstractPotion card, PCLCustomPotionSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            PCLCustomPotionSlot slot = new PCLCustomPotionSlot(cardSlot);
            currentDialog = new PCLCustomPotionEditPotionScreen(slot)
                    .setOnSave(() -> {
                        slot.commitBuilder();
                        AbstractPotion newPotion = slot.getBuilder(0).create();
                        currentSlots.put(newPotion, slot);
                        PCLCustomPotionSlot.getPotions(currentColor).add(slot);
                        grid.add(newPotion);
                    });
        }
    }

    public void duplicateToColor(AbstractPotion card, PCLCustomPotionSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            currentDialog = new PCLCustomCardCopyConfirmationEffect(getAllColors())
                    .addCallback((co) -> {
                        if (co != null) {
                            PCLCustomPotionSlot slot = new PCLCustomPotionSlot(cardSlot, co);
                            open(null, co, this.onClose);
                            currentDialog = new PCLCustomPotionEditPotionScreen(slot)
                                    .setOnSave(() -> {
                                        slot.commitBuilder();
                                        AbstractPotion newPotion = slot.getBuilder(0).create();
                                        currentSlots.put(newPotion, slot);
                                        PCLCustomPotionSlot.getPotions(co).add(slot);
                                        grid.add(newPotion);
                                    });
                        }
                    });
        }
    }

    public void edit(AbstractPotion card, PCLCustomPotionSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            currentDialog = new PCLCustomPotionEditPotionScreen(cardSlot)
                    .setOnSave(() -> {
                        cardSlot.commitBuilder();
                        AbstractPotion newPotion = cardSlot.getBuilder(0).create();
                        grid.remove(card);
                        currentSlots.remove(card);
                        currentSlots.put(newPotion, cardSlot);
                        grid.add(newPotion);
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

    private ArrayList<AbstractPotion> getAvailablePotionsToCopy() {
        return EUIUtils.mapAsNonnull(PCLPotionData.getTemplates(),
                data -> {
                    PCLPotion potion = data.create();
                    return PCLCustomPotionSlot.canFullyCopy(potion) ? potion : null;
                });
    }

    public void loadFromExisting() {
        if (currentDialog == null) {
            currentDialog = new PCLGenericSelectPotionEffect(this.getAvailablePotionsToCopy()).addCallback(card -> {
                if (card instanceof PCLPotion) {
                    PCLCustomPotionSlot slot = new PCLCustomPotionSlot((PCLPotion) card, currentColor);
                    currentDialog = new PCLCustomPotionEditPotionScreen(slot)
                            .setOnSave(() -> {
                                slot.commitBuilder();
                                AbstractPotion newPotion = slot.make();
                                currentSlots.put(newPotion, slot);
                                PCLCustomPotionSlot.getPotions(currentColor).add(slot);
                                grid.add(newPotion);
                            });
                }
            });

        }
    }

    private void makeColorButton(AbstractCard.CardColor co) {
        colorButtons.addButton(button -> open(null, co, this.onClose), EUIGameUtils.getColorName(co))
                .setColor(EUIGameUtils.getColorColor(co));
    }

    private void onPotionClicked(PotionInfo potion) {
        PCLCustomPotionSlot slot = currentSlots.get(potion.potion);
        if (slot != null) {
            edit(potion.potion, slot);
        }
    }

    private void onPotionRightClicked(PotionInfo potion) {
        clickedPotion = potion.potion;
        contextMenu.positionToOpen();
    }

    public void open(AbstractPlayer.PlayerClass playerClass, AbstractCard.CardColor cardColor, ActionT0 onClose) {
        super.open();

        currentClass = playerClass;
        currentColor = EUI.actingColor = cardColor;
        currentSlots.clear();
        grid.clear();
        for (PCLCustomPotionSlot slot : PCLCustomPotionSlot.getPotions(currentColor)) {
            AbstractPotion potion = slot.make();
            currentSlots.put(potion, slot);
            grid.add(potion);
        }
        EUI.potionFilters.initializeForCustomHeader(grid.group, __ -> {
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, currentColor, false, true);
    }

    public void remove(AbstractPotion card, PCLCustomPotionSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            currentDialog = new PCLCustomDeletionConfirmationEffect<PCLCustomPotionSlot>(cardSlot)
                    .addCallback((v) -> {
                        if (v != null) {
                            grid.remove(card);
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
            if (!EUI.potionFilters.isActive) {
                EUI.openPotionFiltersButton.tryRender(sb);
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
            boolean shouldDoStandardUpdate = !EUI.potionFilters.tryUpdate() && !CardCrawlGame.isPopupOpen;
            if (shouldDoStandardUpdate) {
                EUI.openPotionFiltersButton.tryUpdate();
                EUIExporter.exportButton.tryUpdate();
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

    public enum ContextOption {
        Duplicate(PGR.core.strings.cedit_duplicate, PCLCustomPotionSelectorScreen::duplicate),
        DuplicateToColor(PGR.core.strings.cedit_duplicateToColor, PCLCustomPotionSelectorScreen::duplicateToColor),
        Delete(PGR.core.strings.cedit_delete, PCLCustomPotionSelectorScreen::remove);

        public final String name;
        public final ActionT3<PCLCustomPotionSelectorScreen, AbstractPotion, PCLCustomPotionSlot> onSelect;

        ContextOption(String name, ActionT3<PCLCustomPotionSelectorScreen, AbstractPotion, PCLCustomPotionSlot> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}
