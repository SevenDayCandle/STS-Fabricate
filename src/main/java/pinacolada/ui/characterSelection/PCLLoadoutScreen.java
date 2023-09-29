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
import extendedui.interfaces.delegates.ActionT2;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIHeaderlessTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.screen.PCLCardSlotSelectionEffect;
import pinacolada.effects.screen.PCLGenericSelectBlightEffect;
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
    protected static final PCLLoadoutValidation val = new PCLLoadoutValidation();
    protected final ArrayList<PCLBaseStatEditor> baseStatEditors = new ArrayList<>();
    protected final PCLLoadoutData[] presets = new PCLLoadoutData[PCLLoadout.MAX_PRESETS];
    public final EUIContextMenu<ContextOption> contextMenu;
    protected PCLLoadout loadout;
    protected ActionT0 onClose;
    protected int preset;
    protected CharacterOption characterOption;
    protected AbstractPlayerData<?, ?> data;
    protected PCLEffect selectionEffect;
    protected EUILabel startingDeck;
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
    protected PCLLoadoutCanvas canvas;
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

        canvas = new PCLLoadoutCanvas(this);

        attributesText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.57f), screenH(0.84f), buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.loadout_attributesHeader)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.5f);

        seriesButton = new EUIButton(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(startingDeck.hb.x + scale(30), startingDeck.hb.y - scale(65), scale(150), scale(52)))
                .setTooltip(PGR.core.strings.csel_seriesEditor, PGR.core.strings.csel_seriesEditorInfo)
                .setLabel(EUIFontHelper.cardDescriptionFontNormal, 0.9f, PGR.core.strings.csel_seriesEditor)
                .setColor(new Color(0.3f, 0.8f, 0.5f, 1))
                .setOnClick(this::openSeriesSelect);

        presetButtons = new EUIButton[PCLLoadout.MAX_PRESETS];
        for (int i = 0; i < presetButtons.length; i++) {
            //noinspection SuspiciousNameCombination
            presetButtons[i] = new EUIButton(EUIRM.images.squaredButton.texture(), new EUIHitbox(0, 0, buttonHeight, buttonHeight))
                    .setPosition(screenW(0.6f) + ((i - 1f) * buttonHeight), screenH(1f) - (buttonHeight * 0.85f))
                    .setText(String.valueOf(i + 1))
                    .setOnClick(i, this::changePreset)
                    .setOnRightClick(i, this::rightClickPreset);
        }

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
                .setFont(FontHelper.tipHeaderFont, 1);

        cardscountText = new EUITextBox(EUIRM.images.panelRounded.texture(), new EUIHitbox(labelWidth, labelHeight))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setPosition(saveButton.hb.cX, cardsvalueText.hb.y + labelHeight * 1.4f)
                .setFont(FontHelper.tipHeaderFont, 1);

        hindrancevalueText = (EUITextBox) new EUITextBox(EUIRM.images.panelRounded.texture(), new EUIHitbox(labelWidth, labelHeight))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setPosition(saveButton.hb.cX, cardscountText.hb.y + labelHeight * 1.4f)
                .setFont(FontHelper.tipHeaderFont, 1)
                .setTooltip(new EUIHeaderlessTooltip(EUIUtils.format(PGR.core.strings.loadout_hindranceDescription, PCLLoadoutValidation.HINDRANCE_MULTIPLIER)));

        final PCLBaseStatEditor.StatType[] statTypes = PCLBaseStatEditor.StatType.values();
        for (int i = 0; i < statTypes.length; i++) {
            baseStatEditors.add(new PCLBaseStatEditor(statTypes[i], screenW(0.6f), screenH(0.82f - i * 0.1f), this));
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
        refresh();
    }

    public void clear() {
        PCLLoadoutData defaultData = loadout.getDefaultData(preset);
        presets[preset] = defaultData;
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
        return presets[preset];
    }

    public void open(PCLLoadout loadout, AbstractPlayerData<?, ?> data, CharacterOption option, ActionT0 onClose) {
        super.open();

        this.loadout = loadout;
        this.onClose = onClose;
        this.characterOption = option;
        this.data = data;
        this.loadout.onOpen(option);
        EUI.actingColor = data != null ? data.resources.cardColor : option.c.getCardColor();

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
        canvas.initialize(getCurrentPreset());

        seriesButton.setActive(data != null);
        startingDeck.setActive(data != null);
        for (EUIButton button : presetButtons) {
            button.setActive(data != null);
        }

        if (canvas.cardEditors.size() > 0 && canvas.relicsEditors.size() > 0) {
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
                            .setPosition(cardsvalueText.hb.x - cardsvalueText.hb.width * 2, cardsvalueText.hb.y)
            );
        }
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

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        if (selectionEffect != null) {
            selectionEffect.render(sb);
        }
        else {
            seriesButton.tryRender(sb);

            for (EUIButton button : presetButtons) {
                button.tryRender(sb);
            }

            startingDeck.renderImpl(sb);

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
            canvas.tryRender(sb);
        }

        contextMenu.tryRender(sb);
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
        startingDeck.updateImpl();
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
            canvas.updateImpl();
            hindrancevalueText.tryUpdate();
            cardscountText.tryUpdate();
            cardsvalueText.tryUpdate();
            saveButton.tryUpdate();
        }
        contextMenu.tryUpdate();
    }

    public void updateValidation() {
        val.refresh(getCurrentPreset());

        hindrancevalueText.setLabel(PGR.core.strings.loadout_hindranceValue(val.hindranceLevel));
        hindrancevalueText.tooltip.setTitle(hindrancevalueText.label.text);
        cardscountText.setLabel(PGR.core.strings.loadout_cardsCount(val.cardsCount.v1, getCurrentPreset().loadout.minTotalCards))
                .setFontColor(val.cardsCount.v2 ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
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
            screen.refresh();
        });

        public final String name;
        public final ActionT2<PCLLoadoutScreen, Integer> onSelect;

        ContextOption(String name, ActionT2<PCLLoadoutScreen, Integer> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}