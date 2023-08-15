package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIHeaderlessTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.screen.PCLCardSlotSelectionEffect;
import pinacolada.effects.screen.PCLRelicSlotSelectionEffect;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.loadout.PCLLoadoutData;
import pinacolada.resources.loadout.PCLLoadoutValidation;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLLoadoutScreen extends AbstractMenuScreen {
    protected final static PCLLoadoutValidation val = new PCLLoadoutValidation();
    public static final int MAX_CARD_SLOTS = 6;
    public static final int MAX_RELIC_SLOTS = 2;
    protected final ArrayList<PCLCardSlotEditor> slotsEditors = new ArrayList<>();
    protected final ArrayList<PCLRelicSlotEditor> relicsEditors = new ArrayList<>();
    protected final ArrayList<PCLBaseStatEditor> baseStatEditors = new ArrayList<>();
    protected final PCLLoadoutData[] presets = new PCLLoadoutData[PCLLoadout.MAX_PRESETS];
    public final EUIContextMenu<ContextOption> contextMenu;
    protected PCLLoadout loadout;
    protected ActionT0 onClose;
    protected int preset;
    protected CharacterOption characterOption;
    protected AbstractPlayerData<?, ?> data;
    protected PCLCardSlotSelectionEffect cardSelectionEffect;
    protected PCLRelicSlotSelectionEffect relicSelectionEffect;
    protected EUILabel startingDeck;
    protected EUILabel deckText;
    protected EUILabel relicText;
    protected EUILabel attributesText;
    protected EUIButton seriesButton;
    protected EUIButton[] presetButtons;
    protected EUIButton cancelButton;
    protected EUIButton clearButton;
    protected EUIButton saveButton;
    protected EUIToggle upgradeToggle;
    protected EUITextBox cardscountText;
    protected EUITextBox cardsvalueText;
    protected EUITextBox hindrancevalueText;
    protected int rightClickedSlot;
    public PCLBaseStatEditor activeEditor;

    public PCLLoadoutScreen() {
        final float buttonHeight = screenH(0.07f);
        final float labelHeight = screenH(0.04f);
        final float buttonWidth = screenW(0.18f);
        final float labelWidth = screenW(0.20f);
        final float button_cY = buttonHeight * 1.5f;

        startingDeck = new EUILabel(null, new EUIHitbox(screenW(0.18f), screenH(0.05f))
                .setCenter(screenW(0.08f), screenH(0.97f)))
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
                .setSmartText(true, false)
                .setColor(Settings.CREAM_COLOR);

        deckText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.1f), screenH(0.8f), buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.loadout_deckHeader)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.5f);

        relicText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.1f), screenH(0.4f), buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.loadout_relicHeader)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.5f);

        attributesText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.57f), screenH(0.8f), buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.loadout_attributesHeader)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.5f);

        seriesButton = new EUIButton(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(startingDeck.hb.x + scale(30), startingDeck.hb.y - scale(90), scale(150), scale(52)))
                .setTooltip(PGR.core.strings.csel_seriesEditor, PGR.core.strings.csel_seriesEditorInfo)
                .setLabel(EUIFontHelper.cardDescriptionFontNormal, 0.9f, PGR.core.strings.csel_seriesEditor)
                .setColor(new Color(0.3f, 0.8f, 0.5f, 1))
                .setOnClick(this::openSeriesSelect);

        presetButtons = new EUIButton[PCLLoadout.MAX_PRESETS];
        for (int i = 0; i < presetButtons.length; i++) {
            //noinspection SuspiciousNameCombination
            presetButtons[i] = new EUIButton(EUIRM.images.squaredButton.texture(), new EUIHitbox(0, 0, buttonHeight, buttonHeight))
                    .setPosition(screenW(0.45f) + ((i - 1f) * buttonHeight), screenH(1f) - (buttonHeight * 0.85f))
                    .setText(String.valueOf(i + 1))
                    .setOnClick(i, this::changePreset)
                    .setOnRightClick(i, this::rightClickPreset);
        }

        cancelButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(buttonWidth * 0.75f, button_cY)
                .setColor(Color.FIREBRICK)
                .setText(GridCardSelectScreen.TEXT[1])
                .setOnClick(this::close);

        saveButton = EUIButton.createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(screenW(1) - (buttonWidth * 0.75f), button_cY)
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
                .setPosition(saveButton.hb.cX, screenH(0.65f))
                .setFont(FontHelper.tipHeaderFont, 1);

        cardscountText = new EUITextBox(EUIRM.images.panelRounded.texture(), new EUIHitbox(labelWidth, labelHeight))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setPosition(saveButton.hb.cX, cardsvalueText.hb.y + cardsvalueText.hb.height + labelHeight * 0.5f)
                .setFont(FontHelper.tipHeaderFont, 1);

        hindrancevalueText = (EUITextBox) new EUITextBox(EUIRM.images.panelRounded.texture(), new EUIHitbox(labelWidth, labelHeight))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setPosition(saveButton.hb.cX, cardscountText.hb.y + cardscountText.hb.height + labelHeight * 0.5f)
                .setFont(FontHelper.tipHeaderFont, 1)
                .setTooltip(new EUIHeaderlessTooltip(EUIUtils.format(PGR.core.strings.loadout_hindranceDescription, PCLLoadoutValidation.HINDRANCE_MULTIPLIER)));

        for (int i = 0; i < MAX_CARD_SLOTS; i++) {
            slotsEditors.add(new PCLCardSlotEditor(this, screenW(0.1f), screenH(0.75f - (i * 0.05f))));
        }

        for (int i = 0; i < MAX_RELIC_SLOTS; i++) {
            relicsEditors.add(new PCLRelicSlotEditor(this, screenW(0.1f), screenH(0.35f - (i * 0.05f))));
        }

        final PCLBaseStatEditor.StatType[] statTypes = PCLBaseStatEditor.StatType.values();
        for (int i = 0; i < statTypes.length; i++) {
            baseStatEditors.add(new PCLBaseStatEditor(statTypes[i], screenW(0.6f), screenH(0.78f - i * 0.1f), this));
        }

        contextMenu = new EUIContextMenu<ContextOption>(new EUIHitbox(0, 0, 0, 0), o -> o.name)
                .setOnChange(options -> {
                    for (ContextOption o : options) {
                        o.onSelect.invoke(this, rightClickedSlot);
                    }
                })
                .setCanAutosizeButton(true);
    }

    public void changePreset(int preset) {
        this.preset = preset;
        setSlotsActive(true);
    }

    public void clear() {
        PCLLoadoutData defaultData = loadout.getDefaultData(preset);
        presets[preset] = defaultData;
        setSlotsActive(true);
    }

    @Override
    public void dispose() {
        super.dispose();

        toggleViewUpgrades(false);

        if (onClose != null) {
            onClose.invoke();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        PGR.blackScreen.renderImpl(sb);

        if (relicSelectionEffect != null) {
            relicSelectionEffect.render(sb);
        }
        else if (cardSelectionEffect != null) {
            cardSelectionEffect.render(sb);
        }
        else {
            seriesButton.tryRender(sb);

            for (EUIButton button : presetButtons) {
                button.tryRender(sb);
            }

            startingDeck.renderImpl(sb);
            deckText.renderImpl(sb);
            relicText.renderImpl(sb);
            attributesText.renderImpl(sb);

            // All editors must be rendered from top to bottom to prevent dropdowns from overlapping
            for (int i = baseStatEditors.size() - 1; i >= 0; i--) {
                baseStatEditors.get(i).tryRender(sb);
            }

            cancelButton.renderImpl(sb);
            clearButton.renderImpl(sb);
            saveButton.renderImpl(sb);
            upgradeToggle.renderImpl(sb);
            hindrancevalueText.tryRender(sb);
            cardscountText.tryRender(sb);
            cardsvalueText.tryRender(sb);

            for (int i = relicsEditors.size() - 1; i >= 0; i--) {
                relicsEditors.get(i).tryRender(sb);
            }

            for (int i = slotsEditors.size() - 1; i >= 0; i--) {
                slotsEditors.get(i).tryRender(sb);
            }
        }

        contextMenu.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();

        PGR.blackScreen.updateImpl();
        startingDeck.updateImpl();
        deckText.updateImpl();
        relicText.updateImpl();
        attributesText.updateImpl();
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade).updateImpl();

        if (cardSelectionEffect != null) {
            cardSelectionEffect.update();

            if (cardSelectionEffect.isDone) {
                cardSelectionEffect = null;
                setSlotsActive(true);
            }
        }
        else if (relicSelectionEffect != null) {
            relicSelectionEffect.update();

            if (relicSelectionEffect.isDone) {
                relicSelectionEffect = null;
                setSlotsActive(true);
            }
        }
        else {
            seriesButton.tryUpdate();

            if (!EUI.doesActiveElementExist()) {
                for (int i = 0; i < presetButtons.length; i++) {
                    final EUIButton button = presetButtons[i];
                    button
                            .setColor((i == preset) ? Color.SKY : button.interactable ? Color.LIGHT_GRAY : Color.DARK_GRAY)
                            .tryUpdate();
                }
            }

            for (PCLBaseStatEditor beditor : baseStatEditors) {
                if (activeEditor == null || activeEditor == beditor) {
                    beditor.tryUpdate();
                }
            }

            cancelButton.updateImpl();
            clearButton.updateImpl();
            saveButton.updateImpl();

            for (PCLCardSlotEditor editor : slotsEditors) {
                editor.tryUpdate();
            }
            for (PCLRelicSlotEditor editor : relicsEditors) {
                editor.tryUpdate();
            }
        }

        hindrancevalueText.tryUpdate();
        cardscountText.tryUpdate();
        cardsvalueText.tryUpdate();
        saveButton.tryUpdate();

        contextMenu.tryUpdate();
    }

    public void open(PCLLoadout loadout, AbstractPlayerData<?, ?> data, CharacterOption option, ActionT0 onClose) {
        super.open();

        CardCrawlGame.mainMenuScreen.superDarken = false;
        this.loadout = loadout;
        this.onClose = onClose;
        this.characterOption = option;
        this.data = data;
        this.loadout.onOpen(option);

        for (int i = 0; i < loadout.presets.length; i++) {
            presets[i] = loadout.getPreset(i).makeCopy();
        }

        for (PCLBaseStatEditor beditor : baseStatEditors) {
            final boolean available = GameUtilities.getMaxAscensionLevel(option.c) >= beditor.type.unlockLevel;
            beditor.setActive(available);
            beditor.setInteractable(available);
            if (!available) {
                for (int i = 0; i < loadout.presets.length; i++) {
                    presets[i].values.put(beditor.type, 0);
                }
            }
        }

        startingDeck.setLabel(PGR.core.strings.csel_leftText + EUIUtils.SPLIT_LINE + PCLCoreStrings.colorString("y", loadout.getName()));

        toggleViewUpgrades(false);
        changePreset(loadout.preset);

        seriesButton.setActive(data != null);
        startingDeck.setActive(data != null);
        for (EUIButton button : presetButtons) {
            button.setActive(data != null);
        }

        EUITourTooltip.queueFirstView(PGR.config.tourLoadout,
                new EUITourTooltip(deckText.hb, deckText.text, PGR.core.strings.loadout_tutorialCard)
                        .setFlash(slotsEditors.get(0).cardnameText.image),
                new EUITourTooltip(slotsEditors.get(0).cardvalueText.hb, deckText.text, PGR.core.strings.loadout_tutorialValue)
                        .setFlash(slotsEditors.get(0).cardvalueText.image),
                slotsEditors.get(0).decrementButton.makeTour(true),
                slotsEditors.get(0).addButton.makeTour(true),
                slotsEditors.get(0).clearButton.makeTour(true),
                slotsEditors.get(0).changeButton.makeTour(true),
                new EUITourTooltip(relicText.hb, relicText.text, PGR.core.strings.loadout_tutorialRelic)
                        .setFlash(relicsEditors.get(0).relicValueText.image),
                new EUITourTooltip(attributesText.hb, attributesText.text, PGR.core.strings.loadout_tutorialAttributes),
                new EUITourTooltip(cardsvalueText.hb, PGR.core.strings.csel_deckEditor, PGR.core.strings.loadout_tutorialRequired)
                        .setFlash(cardsvalueText.image)
                        .setPosition(cardsvalueText.hb.x - cardsvalueText.hb.width * 2, cardsvalueText.hb.y)
        );
    }

    private void openSeriesSelect() {
        if (characterOption != null && data != null) {
            PGR.seriesSelection.open(characterOption, data, this.onClose);
        }
    }

    public void repositionSlotEditor(PCLCardSlotEditor cardSlotEditor, int index) {
        cardSlotEditor.translate(screenW(0.1f), screenH(0.75f - (index * 0.05f)));
    }

    public void rightClickPreset(int preset) {
        rightClickedSlot = preset;
        if (rightClickedSlot != this.preset) {
            ArrayList<ContextOption> list = new ArrayList<>();
            list.add(ContextOption.CopyFrom);
            list.add(ContextOption.CopyTo);

            contextMenu.setItems(list);
            contextMenu.positionToOpen();
        }

    }

    public void save() {
        for (int i = 0, presetsLength = presets.length; i < presetsLength; i++) {
            loadout.presets[i] = presets[i];
        }

        loadout.preset = preset;
        if (data != null) {
            data.saveLoadouts();
        }
        close();
    }

    public void setSlotsActive(boolean active) {
        if (active) {
            final PCLLoadoutData data = presets[preset];
            for (int i = 0; i < slotsEditors.size(); i++) {
                final PCLCardSlotEditor editor = slotsEditors.get(i);
                editor.setActive(data.cardsSize() > i);
                editor.setSlot(editor.isActive ? data.getCardSlot(i) : null);
            }
            for (int i = 0; i < relicsEditors.size(); i++) {
                final PCLRelicSlotEditor reditor = relicsEditors.get(i);
                reditor.setActive(data.relicsSize() > i);
                reditor.setSlot(reditor.isActive ? data.getRelicSlot(i) : null);
            }
            for (PCLBaseStatEditor beditor : baseStatEditors) {
                beditor.setLoadout(loadout, data);
            }
            val.refresh(presets[preset]);
        }
        else {
            for (PCLCardSlotEditor editor : slotsEditors) {
                editor.setActive(false);
            }
        }
        updateValidation();
    }

    public void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
    }

    public void trySelectCard(PCLCardSlotEditor cardSlot) {
        cardSelectionEffect = new PCLCardSlotSelectionEffect(cardSlot);
        setSlotsActive(false);
    }

    public void trySelectRelic(PCLRelicSlotEditor relicSlot) {
        relicSelectionEffect = new PCLRelicSlotSelectionEffect(relicSlot);
        setSlotsActive(false);
    }

    public void updateValidation() {
        val.refresh(presets[preset]);

        hindrancevalueText.setLabel(PGR.core.strings.loadout_hindranceValue(val.hindranceLevel));
        hindrancevalueText.tooltip.setTitle(hindrancevalueText.label.text);
        cardscountText.setLabel(PGR.core.strings.loadout_cardsCount(val.cardsCount.v1)).setFontColor(val.cardsCount.v2 ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        cardsvalueText
                .setLabel(PGR.core.strings.loadout_totalValue(val.totalValue.v1, loadout.maxValue < 0 ? PGR.core.strings.subjects_infinite : loadout.maxValue))
                .setFontColor(val.totalValue.v2 ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);

        saveButton.setInteractable(val.isValid);
        if (val.isValid) {
            saveButton.setTooltip(null);
        }
        else {
            saveButton.setTooltip(new EUITooltip(PGR.core.strings.loadout_invalidLoadout, val.getFailingString()));
        }
    }

    public enum ContextOption {
        CopyTo(PGR.core.strings.loadout_copyTo, (screen, index) -> {
            screen.presets[index] = screen.presets[screen.preset];
            screen.changePreset(index);
        }),
        CopyFrom(PGR.core.strings.loadout_copyFrom, (screen, index) -> {
            screen.presets[screen.preset] = screen.presets[index];
            screen.setSlotsActive(true);
        });

        public final String name;
        public final ActionT2<PCLLoadoutScreen, Integer> onSelect;

        ContextOption(String name, ActionT2<PCLLoadoutScreen, Integer> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}