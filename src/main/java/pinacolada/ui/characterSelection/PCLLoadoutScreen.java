package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIHeaderlessTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.screen.*;
import pinacolada.resources.PCLPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.loadout.PCLLoadoutData;
import pinacolada.resources.loadout.PCLLoadoutDataInfo;
import pinacolada.resources.loadout.PCLLoadoutValidation;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.ui.editor.card.EditDeleteDropdownRow;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLLoadoutScreen extends AbstractMenuScreen {
    protected static final PCLLoadoutValidation val = new PCLLoadoutValidation();
    protected final ArrayList<PCLBaseStatEditor> baseStatEditors = new ArrayList<>();
    protected EUIDropdown<PCLLoadoutData> presetDropdown;
    protected PCLLoadout loadout;
    protected ActionT0 onClose;
    protected CharacterOption characterOption;
    protected PCLPlayerData<?, ?, ?> data;
    protected PCLEffect selectionEffect;
    protected EUILabel attributesText;
    protected EUIButton addPresetButton;
    protected EUIButton seriesButton;
    protected EUIButton cancelButton;
    protected EUIButton clearButton;
    protected EUIButton saveButton;
    protected EUIToggle upgradeToggle;
    protected EUITextBox cardscountText;
    protected EUITextBox cardsvalueText;
    protected PCLLoadoutCanvas canvas;
    private PCLLoadoutData current;
    public PCLBaseStatEditor activeEditor;

    public PCLLoadoutScreen() {
        final float buttonHeight = screenH(0.07f);
        final float labelHeight = screenH(0.04f);
        final float buttonWidth = screenW(0.18f);
        final float labelWidth = screenW(0.20f);
        final float button_cY = buttonHeight * 1.5f;

        canvas = new PCLLoadoutCanvas(this);

        attributesText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.57f), screenH(0.84f), buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.loadout_attributesHeader)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.5f);

        seriesButton = new EUIButton(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(screenW(0.01f), screenH(0.93f), scale(150), scale(52)))
                .setTooltip(PGR.core.strings.csel_seriesEditor, PGR.core.strings.csel_seriesEditorInfo)
                .setLabel(EUIFontHelper.cardDescriptionFontNormal, 0.9f, PGR.core.strings.csel_seriesEditor)
                .setColor(new Color(0.3f, 0.8f, 0.5f, 1))
                .setOnClick(this::openSeriesSelect);

        presetDropdown = new EUIDropdown<PCLLoadoutData>(new EUIHitbox(screenW(0.8f), screenH(0.92f), scale(200), scale(40)), l -> l.name)
                .setOnChange(l -> {
                    if (!l.isEmpty()) {
                        this.changePreset(l.get(0));
                    }
                })
                .setRowFunction((a, b, c, d) -> new EditDeleteDropdownRow<PCLLoadoutData, PCLLoadoutData>(a, b, c, c, d, f -> this.openPresetEditName(PGR.core.strings.cedit_renameItem, f), this::openPresetDelete))
                .setRowWidthFunction((a, b, c) -> a.calculateRowWidth() + scale(250))
                .setHeader(EUIFontHelper.cardTooltipTitleFontNormal, 0.8f, Settings.CREAM_COLOR, EUIUtils.EMPTY_STRING)
                .setTooltip(PGR.core.strings.loadout_changePreset, PGR.core.strings.loadout_tutorialPreset);
        addPresetButton = new EUIButton(EUIRM.images.plus.texture(), new EUIHitbox(screenW(0.7f), screenH(0.87f), scale(30), scale(30)))
                .setOnClick(() -> this.openPresetCreate(PGR.core.strings.loadout_newPreset))
                .setTooltip(PGR.core.strings.loadout_newPreset, PGR.core.strings.loadout_tutorialPreset);

        cancelButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(buttonWidth * 0.85f, button_cY)
                .setColor(Color.FIREBRICK)
                .setText(GridCardSelectScreen.TEXT[1])
                .setOnClick(this::close);

        saveButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(screenW(1) - (buttonWidth * 0.85f), button_cY)
                .setColor(Color.FOREST)
                .setText(GridCardSelectScreen.TEXT[0])
                .setInteractable(false)
                .setOnClick(this::save);

        clearButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(saveButton.hb.cX, saveButton.hb.y + saveButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.loadout_reset)
                .setOnClick(this::clear);

        upgradeToggle = new EUIToggle(new EUIHitbox(0, 0, labelWidth * 0.75f, labelHeight))
                .setPosition(screenW(0.5f), screenH(0.055f))
                .setBackground(EUIRM.images.panelRounded.texture(), new Color(0, 0, 0, 0.85f))
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(this::toggleViewUpgrades);

        cardsvalueText = new EUITextBox(EUIRM.images.panelRounded.texture(), new EUIHitbox(labelWidth, labelHeight))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setPosition(saveButton.hb.cX, screenH(0.7f))
                .setFont(EUIFontHelper.cardTooltipTitleFontNormal, 1);

        cardscountText = new EUITextBox(EUIRM.images.panelRounded.texture(), new EUIHitbox(labelWidth, labelHeight))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setPosition(saveButton.hb.cX, cardsvalueText.hb.y + labelHeight * 1.4f)
                .setFont(EUIFontHelper.cardTooltipTitleFontNormal, 1);

        final PCLBaseStatEditor.StatType[] statTypes = PCLBaseStatEditor.StatType.values();
        for (int i = 0; i < statTypes.length; i++) {
            baseStatEditors.add(new PCLBaseStatEditor(statTypes[i], screenW(0.6f), screenH(0.82f - i * 0.1f), this));
        }
    }

    protected PCLLoadoutData addPreset() {
        PCLLoadoutData newLoadout = loadout.getDefaultData();
        presetDropdown.addItems(newLoadout);
        refreshPresetSelector();
        changePreset(newLoadout);
        return newLoadout;
    }

    public void changePreset(String key) {
        for (PCLLoadoutData data : presetDropdown.getAllItems()) {
            if (data.ID.equals(key)) {
                changePreset(data);
                return;
            }
        }
        if (presetDropdown.size() > 0) {
            changePreset(presetDropdown.getItemAt(0));
        }
    }

    public void changePreset(PCLLoadoutData preset) {
        current = preset;
        presetDropdown.setSelection(current, false);
        refresh();
    }

    public void clear() {
        loadout.resetPreset(current);
        refresh();
    }

    @Override
    public void dispose() {
        super.dispose();

        toggleViewUpgrades(false);

        if (onClose != null) {
            onClose.invoke();
        }
    }

    public PCLLoadoutData getCurrentPreset() {
        return current;
    }

    public void open(PCLLoadout loadout, PCLPlayerData<?, ?, ?> data, CharacterOption option, ActionT0 onClose) {
        super.open();

        this.loadout = loadout;
        this.onClose = onClose;
        this.characterOption = option;
        this.data = data;
        EUI.actingColor = data != null ? data.resources.cardColor : option.c.getCardColor();

        ArrayList<PCLLoadoutData> dataCopies = EUIUtils.map(loadout.presets.values(), PCLLoadoutData::makeCopy);
        for (PCLBaseStatEditor beditor : baseStatEditors) {
            final boolean available = GameUtilities.getMaxAscensionLevel(option.c) >= beditor.type.unlockLevel;
            beditor.setActive(available);
            beditor.setInteractable(available);
            if (!available) {
                for (PCLLoadoutData ld : dataCopies) {
                    ld.values.put(beditor.type, 0);
                }
            }
        }

        if (dataCopies.isEmpty()) {
            PCLLoadoutData dData = loadout.getDefaultData();
            presetDropdown.setItems(dData);
            changePreset(dData);
        }
        else {
            presetDropdown.setItems(dataCopies);
            changePreset(loadout.preset);
        }
        refreshPresetSelector();

        presetDropdown.setHeaderText(loadout.getName());

        toggleViewUpgrades(false);
        canvas.initialize(getCurrentPreset());

        seriesButton.setActive(data != null && data.hasLoadouts());
        presetDropdown.setActive(data != null);
        addPresetButton.setActive(data != null);

        if (!canvas.cardEditors.isEmpty() && !canvas.relicsEditors.isEmpty()) {
            EUITourTooltip.queueFirstView(PGR.config.tourLoadout,
                    new EUITourTooltip(canvas.deckText.hb, canvas.deckText.text, PGR.core.strings.loadout_tutorialCard)
                            .setFlash(canvas.cardEditors.get(0).nameText.image),
                    new EUITourTooltip(canvas.cardEditors.get(0).cardvalueText.hb, canvas.deckText.text, PGR.core.strings.loadout_tutorialValue)
                            .setFlash(canvas.cardEditors.get(0).cardvalueText.image),
                    canvas.cardEditors.get(0).decrementButton.makeTour(true),
                    canvas.cardEditors.get(0).addButton.makeTour(true),
                    canvas.cardEditors.get(0).clearButton.makeTour(true),
                    canvas.cardEditors.get(0).changeButton.makeTour(true),
                    new EUITourTooltip(canvas.relicText.hb, canvas.relicText.text, PGR.core.strings.loadout_tutorialRelic)
                            .setFlash(canvas.relicsEditors.get(0).relicValueText.image),
                    new EUITourTooltip(attributesText.hb, attributesText.text, PGR.core.strings.loadout_tutorialAttributes),
                    new EUITourTooltip(cardsvalueText.hb, PGR.core.strings.csel_deckEditor, PGR.core.strings.loadout_tutorialRequired)
                            .setFlash(cardsvalueText.image)
                            .setPosition(cardsvalueText.hb.x - cardsvalueText.hb.width * 2, cardsvalueText.hb.y),
                    presetDropdown.makeTour(true)
            );
        }
    }

    protected void openPresetCreate(String title) {
        selectionEffect = new PCLCustomPresetNameDialogEffect(title, EUIRM.strings.generic2(loadout.getName(), presetDropdown.size() + 1))
                .addCallback(res -> {
                    if (res != null) {
                        addPreset().name = res;
                        presetDropdown.refreshText();
                        refreshPresetSelector();
                    }
                });
    }

    protected void openPresetDelete(PCLLoadoutData flag) {
        if (presetDropdown.size() > 1) {
            selectionEffect = new PCLCustomDeletionConfirmationEffect<>(flag)
                    .addCallback((v) -> {
                        if (v != null) {
                            ArrayList<PCLLoadoutData> items = presetDropdown.getAllItems();
                            items.remove(v);
                            presetDropdown.setItems(items);
                            if (current == v) {
                                changePreset(presetDropdown.getItemAt(0));
                            }
                            refreshPresetSelector();
                        }
                    });
        }
    }

    protected void openPresetEditName(String title, PCLLoadoutData flag) {
        selectionEffect = new PCLCustomPresetNameDialogEffect(title, flag.name)
                .addCallback(res -> {
                    if (res != null) {
                        flag.name = res;
                        presetDropdown.refreshText();
                        refreshPresetSelector();
                    }
                });
    }

    private void openSeriesSelect() {
        if (characterOption != null && data != null) {
            PGR.seriesSelection.open(characterOption, data, this.onClose);
        }
    }

    protected void refresh() {
        for (PCLBaseStatEditor beditor : baseStatEditors) {
            beditor.setLoadout(loadout, getCurrentPreset());
        }
        canvas.initialize(getCurrentPreset());
        updateValidation();
    }

    protected void refreshPresetSelector() {
        presetDropdown.sortByLabel();
        addPresetButton.setPosition(presetDropdown.getClearButtonHitbox().cX + presetDropdown.getClearButtonHitbox().width, presetDropdown.getClearButtonHitbox().y + addPresetButton.hb.height * 0.25f);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        if (selectionEffect != null) {
            selectionEffect.render(sb);
        }
        else {
            seriesButton.tryRender(sb);
            presetDropdown.tryRender(sb);
            addPresetButton.tryRender(sb);
            attributesText.renderImpl(sb);

            // All editors must be rendered from top to bottom to prevent dropdowns from overlapping
            for (int i = baseStatEditors.size() - 1; i >= 0; i--) {
                baseStatEditors.get(i).tryRender(sb);
            }

            cancelButton.renderImpl(sb);
            clearButton.renderImpl(sb);
            saveButton.renderImpl(sb);
            upgradeToggle.renderImpl(sb);
            cardscountText.tryRender(sb);
            cardsvalueText.tryRender(sb);
            canvas.tryRender(sb);
        }
    }

    public void save() {
        loadout.presets.clear();
        for (PCLLoadoutData ld : presetDropdown.getAllItems()) {
            loadout.presets.put(ld.ID, ld);
        }
        loadout.preset = current.ID;
        for (PCLLoadoutData data : loadout.presets.values()) {
            if (data == null) {
                continue;
            }
            PCLLoadoutDataInfo info = new PCLLoadoutDataInfo(loadout.ID, data);
            info.commit();
        }
        if (data != null) {
            data.saveSelectedLoadout();
        }
        close();
    }

    public void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
    }

    public void trySelectAbility(PCLAbilityEditor slot) {
        selectionEffect = new PCLGenericSelectBlightEffect(EUIUtils.mapAsNonnull(slot.getAvailableAbilitiesForSelection(), BlightHelper::getBlight))
                .addCallback(a -> {
                    if (a != null && !a.blightID.equals(slot.slot.selected)) {
                        slot.slot.select(a.blightID);
                    }
                });
    }

    public PCLCardSlotSelectionEffect trySelectCard(PCLCardSlotEditor cardSlot) {
        PCLCardSlotSelectionEffect effect = new PCLCardSlotSelectionEffect(cardSlot);
        selectionEffect = effect;
        return effect;
    }

    public PCLRelicSlotSelectionEffect trySelectRelic(PCLRelicSlotEditor relicSlot) {
        PCLRelicSlotSelectionEffect effect = new PCLRelicSlotSelectionEffect(relicSlot);
        selectionEffect = effect;
        return effect;
    }

    @Override
    public void updateImpl() {
        if (EUIInputManager.tryEscape()) {
            if (selectionEffect != null) {
                selectionEffect = null;
            }
            else {
                close();
                return;
            }
        }

        CardCrawlGame.mainMenuScreen.screenColor.a = MathHelper.popLerpSnap(CardCrawlGame.mainMenuScreen.screenColor.a, 0.8F);
        attributesText.updateImpl();
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade).updateImpl();

        if (selectionEffect != null) {
            selectionEffect.update();

            if (selectionEffect.isDone) {
                selectionEffect = null;
            }
        }
        else {
            seriesButton.tryUpdate();
            presetDropdown.tryUpdate();
            addPresetButton.tryUpdate();

            for (PCLBaseStatEditor beditor : baseStatEditors) {
                if (activeEditor == null || activeEditor == beditor) {
                    beditor.tryUpdate();
                }
            }

            cancelButton.updateImpl();
            clearButton.updateImpl();
            saveButton.updateImpl();
            canvas.updateImpl();
            cardscountText.tryUpdate();
            cardsvalueText.tryUpdate();
            saveButton.tryUpdate();
        }
    }

    public void updateValidation() {
        val.refresh(getCurrentPreset());

        cardscountText.setLabel(PGR.core.strings.loadout_cardsCount(val.cardsCount.v1, getCurrentPreset().loadout.minTotalCards))
                .setFontColor(val.cardsCount.v2 ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        if (loadout.maxValue < 0) {
            cardsvalueText.setActive(false);
        }
        else {
            cardsvalueText
                    .setLabel(PGR.core.strings.loadout_totalValue(val.totalValue.v1, loadout.maxValue))
                    .setFontColor(val.totalValue.v2 ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR)
                    .setActive(true);
        }

        saveButton.setInteractable(val.isValid);
        if (val.isValid) {
            saveButton.setTooltip(null);
        }
        else {
            saveButton.setTooltip(new EUITooltip(PGR.core.strings.loadout_invalidLoadout, val.getFailingString()));
        }
    }
}