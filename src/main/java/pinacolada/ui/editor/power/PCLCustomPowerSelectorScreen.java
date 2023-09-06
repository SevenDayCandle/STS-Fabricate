package pinacolada.ui.editor.power;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
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
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.PCLCustomCardCopyConfirmationEffect;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.PCLPowerRenderable;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLPowerGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PCLCustomPowerSelectorScreen extends AbstractMenuScreen {
    private static final float DRAW_START_X = (Settings.WIDTH - (5f * AbstractCard.IMG_WIDTH * 0.75f) - (4f * Settings.CARD_VIEW_PAD_X) + AbstractCard.IMG_WIDTH * 0.75f);
    private static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    private static final float PAD_Y = AbstractCard.IMG_HEIGHT * 0.75f + Settings.CARD_VIEW_PAD_Y;
    private static final float SCROLL_BAR_THRESHOLD = 500f * Settings.scale;
    protected static final float ITEM_HEIGHT = AbstractCard.IMG_HEIGHT * 0.15f;
    public static AbstractPlayer.PlayerClass currentClass;
    protected final PCLPowerGrid grid;
    protected final EUIToggle toggle;
    private PCLPowerRenderable clickedPower;
    protected ActionT0 onClose;
    protected EUIButton addButton;
    protected EUIButton cancelButton;
    protected EUIButton openButton;
    protected EUIButton reloadButton;
    protected EUIContextMenu<ContextOption> contextMenu;
    protected EUITextBox info;
    protected HashMap<PCLPowerRenderable, PCLCustomPowerSlot> currentSlots = new HashMap<>();
    protected PCLEffectWithCallback<?> currentDialog;

    public PCLCustomPowerSelectorScreen() {
        final float buttonHeight = screenH(0.06f);
        final float labelHeight = screenH(0.04f);
        final float buttonWidth = screenW(0.18f);
        final float labelWidth = screenW(0.20f);

        this.grid = (PCLPowerGrid) new PCLPowerGrid(1f)
                .setOnClick(this::onPowerClicked)
                .setOnRightClick(this::onPowerRightClicked);
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

        openButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(addButton.hb.cX, addButton.hb.y + addButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_openFolder)
                .setOnClick(PCLCustomPowerSelectorScreen::openFolder);

        reloadButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(openButton.hb.cX, openButton.hb.y + openButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_reloadCards)
                .setTooltip(PGR.core.strings.cedit_reloadCards, PGR.core.strings.cetut_selectorReload)
                .setOnClick(PCLCustomPowerSlot::initialize);

        contextMenu = (EUIContextMenu<ContextOption>) new EUIContextMenu<ContextOption>(new EUIHitbox(-500f, -500f, 0, 0), o -> o.name)
                .setItems(ContextOption.values())
                .setOnChange(options -> {
                    for (ContextOption o : options) {
                        o.onSelect.invoke(this, clickedPower, currentSlots.get(clickedPower));
                    }
                })
                .setCanAutosizeButton(true);
        info = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(screenW(0.25f), screenH(0.035f), screenW(0.5f), buttonHeight * 2f))
                .setLabel(PGR.core.strings.cetut_selector2)
                .setAlignment(0.75f, 0.05f, true)
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 0.9f);
    }

    public static void openFolder() {
        try {
            Desktop.getDesktop().open(Gdx.files.local(PCLCustomLoadable.FOLDER).file());
        }
        catch (Exception e) {
            EUIUtils.logError(null, "Failed to open power folder.");
        }
    }

    public void add() {
        if (currentDialog == null) {
            PCLCustomPowerSlot slot = new PCLCustomPowerSlot();
            currentDialog = new PCLCustomPowerEditPowerScreen(slot)
                    .setOnSave(() -> {
                        PCLCustomPowerSlot.addSlot(slot);
                        PCLPowerRenderable newPower = slot.makeRenderable();
                        currentSlots.put(newPower, slot);
                        grid.group.group = PGR.powerHeader.originalGroup;
                        grid.add(newPower);
                        refreshGrid();
                    });
        }
    }

    public void duplicate(PCLPowerRenderable card, PCLCustomPowerSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            PCLCustomPowerSlot slot = new PCLCustomPowerSlot(cardSlot);
            currentDialog = new PCLCustomPowerEditPowerScreen(slot)
                    .setOnSave(() -> {
                        PCLCustomPowerSlot.addSlot(slot);
                        PCLPowerRenderable newPower = slot.makeRenderable();
                        currentSlots.put(newPower, slot);
                        grid.group.group = PGR.powerHeader.originalGroup;
                        grid.add(newPower);
                        refreshGrid();
                    });
        }
    }

    public void edit(PCLPowerRenderable card, PCLCustomPowerSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            String prev = cardSlot.ID;
            currentDialog = new PCLCustomPowerEditPowerScreen(cardSlot)
                    .setOnSave(() -> {
                        PCLCustomPowerSlot.editSlot(cardSlot, prev);
                        PCLPowerRenderable newPower = cardSlot.makeRenderable();
                        grid.group.group = PGR.powerHeader.originalGroup;
                        grid.remove(card);
                        currentSlots.remove(card);
                        currentSlots.put(newPower, cardSlot);
                        grid.add(newPower);
                        refreshGrid();
                    });
        }
    }

    private void onPowerClicked(PCLPowerRenderable relic) {
        PCLCustomPowerSlot slot = currentSlots.get(relic);
        if (slot != null) {
            edit(relic, slot);
        }
    }

    private void onPowerRightClicked(PCLPowerRenderable card) {
        clickedPower = card;
        contextMenu.positionToOpen();
    }

    public void open(AbstractPlayer.PlayerClass playerClass, AbstractCard.CardColor cardColor, ActionT0 onClose) {
        super.open();

        currentClass = playerClass;
        EUI.actingColor = cardColor;
        currentSlots.clear();
        grid.clear();
        for (PCLCustomPowerSlot slot : PCLCustomPowerSlot.getAll().values()) {
            PCLPowerRenderable relic = slot.makeRenderable();
            currentSlots.put(relic, slot);
            grid.add(relic);
        }
        refreshGrid();
    }

    public void refreshGrid() {
        PGR.powerFilters.initializeForCustomHeader(grid.group, __ -> {
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, AbstractCard.CardColor.COLORLESS, false, true);
    }

    public void remove(PCLPowerRenderable card, PCLCustomPowerSlot cardSlot) {
        if (currentDialog == null && cardSlot != null) {
            currentDialog = new PCLCustomDeletionConfirmationEffect<PCLCustomPowerSlot>(cardSlot)
                    .addCallback((v) -> {
                        if (v != null) {
                            grid.group.group = PGR.powerHeader.originalGroup;
                            grid.remove(card);
                            currentSlots.remove(card);
                            v.wipeBuilder();
                            refreshGrid();
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
            reloadButton.tryRender(sb);
            contextMenu.tryRender(sb);
            if (!EUI.relicFilters.isActive) {
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
            boolean shouldDoStandardUpdate = !EUI.relicFilters.tryUpdate() && !CardCrawlGame.isPopupOpen;
            if (shouldDoStandardUpdate) {
                EUI.openFiltersButton.tryUpdate();
                EUIExporter.exportButton.tryUpdate();
                info.tryUpdate();
                grid.tryUpdate();
                cancelButton.tryUpdate();
                addButton.tryUpdate();
                openButton.tryUpdate();
                reloadButton.tryUpdate();
            }
            EUIExporter.exportDropdown.tryUpdate();
        }
    }

    public enum ContextOption {
        Duplicate(PGR.core.strings.cedit_duplicate, PCLCustomPowerSelectorScreen::duplicate),
        Delete(PGR.core.strings.cedit_delete, PCLCustomPowerSelectorScreen::remove);

        public final String name;
        public final ActionT3<PCLCustomPowerSelectorScreen, PCLPowerRenderable, PCLCustomPowerSlot> onSelect;

        ContextOption(String name, ActionT3<PCLCustomPowerSelectorScreen, PCLPowerRenderable, PCLCustomPowerSlot> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}
