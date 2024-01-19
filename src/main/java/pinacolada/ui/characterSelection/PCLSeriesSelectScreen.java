package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.cardFilter.CardKeywordFilters;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUICardGrid;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.screens.CustomCardLibraryScreen;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.*;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.screen.ViewInGameCardPoolEffect;
import pinacolada.effects.screen.ViewInGameRelicPoolEffect;
import pinacolada.resources.PCLPlayerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLCustomLoadout;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.utilities.GameUtilities;

import java.util.*;
import java.util.stream.Collectors;

// Copied and modified from STS-AnimatorMod
public class PCLSeriesSelectScreen extends AbstractMenuScreen {
    public final EUICardGrid cardGrid;
    public final EUIButton resetPoolButton;
    public final EUIButton resetBanButton;
    public final EUIButton previewCards;
    public final EUIButton colorlessButton;
    public final EUIButton cancel;
    public final EUIButton confirm;
    public final EUIButton loadoutEditor;
    public final EUIButton relicsButton;
    public final EUITextBox statsPanel;
    public final EUITextBox previewCardsInfo;
    public final EUIContextMenu<ContextOption> contextMenu;
    public final ArrayList<AbstractCard> shownCards = new ArrayList<>();
    public final ArrayList<AbstractCard> shownColorlessCards = new ArrayList<>();
    public final ArrayList<ChoiceCard<PCLLoadout>> loadouts = new ArrayList<>();
    public final HashMap<String, AbstractCard> allCards = new HashMap<>();
    public final HashMap<String, AbstractCard> allColorlessCards = new HashMap<>();
    public final HashSet<String> bannedCards = new HashSet<>();
    public final HashSet<String> bannedColorless = new HashSet<>();
    public final HashSet<String> selectedLoadouts = new HashSet<>();
    protected PCLPlayerData<?, ?, ?> data;
    protected ActionT0 onClose;
    protected CharacterOption characterOption;
    protected ChoiceCard<PCLLoadout> selectedCard;
    protected PCLEffect currentEffect;
    protected boolean isCustom;
    protected int totalCardsCache = 0;
    protected int totalColorlessCache = 0;
    public ChoiceCard<PCLLoadout> currentSeriesCard;
    private boolean isScreenDisabled;

    public PCLSeriesSelectScreen() {
        final Texture panelTexture = EUIRM.images.panelRounded.texture();
        final FuncT1<Float, Float> getY = (delta) -> screenH(0.95f) - screenH(0.07f * delta);
        final float buttonHeight = screenH(0.05f);
        final float buttonWidth = screenW(0.17f);
        final float xPos = screenW(0.82f);

        cardGrid = (EUICardGrid) new EUICardGrid(0.31f, false)
                .setOnClick(this::selectCard)
                .setOnRightClick(this::onCardRightClicked)
                .showScrollbar(false);

        loadoutEditor = new EUIButton(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(screenW(0.01f), screenH(0.93f), scale(150), scale(52)))
                .setTooltip(PGR.core.strings.csel_deckEditor, PGR.core.strings.csel_deckEditorInfo)
                .setLabel(FontHelper.cardDescFont_N, 0.9f, PGR.core.strings.csel_deckEditor)
                .setColor(new Color(0.3f, 0.5f, 0.8f, 1))
                .setOnClick(this::openLoadoutEditor);

        colorlessButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(loadoutEditor.hb.x, loadoutEditor.hb.y - scale(65), scale(150), scale(52)))
                .setTooltip(PGR.core.strings.sui_showColorless, PGR.core.strings.sui_showColorlessInfo)
                .setLabel(FontHelper.cardDescFont_N, 0.9f, PGR.core.strings.sui_showColorless)
                .setColor(new Color(0.5f, 0.3f, 0.8f, 1))
                .setOnClick(this::previewColorless);

        relicsButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(colorlessButton.hb.x, colorlessButton.hb.y - scale(65), scale(150), scale(52)))
                .setTooltip(PGR.core.strings.sui_relicPool, PGR.core.strings.sui_relicPoolInfo)
                .setLabel(FontHelper.cardDescFont_N, 0.9f, PGR.core.strings.sui_relicPool)
                .setColor(new Color(0.8f, 0.3f, 0.5f, 1))
                .setOnClick(this::previewRelics);

        Color panelColor = new Color(0.08f, 0.08f, 0.08f, 1);
        previewCardsInfo = new EUITextBox(panelTexture, new EUIHitbox(xPos, getY.invoke(1.9f), buttonWidth, screenH(0.13f)))
                .setLabel(EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, PGR.core.strings.sui_instructions1, PGR.core.strings.sui_instructions2))
                .setAlignment(0.85f, 0.1f, true)
                .setColors(panelColor, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.tooltipFont, 1f);
        statsPanel = new EUITextBox(panelTexture, new EUIHitbox(xPos, getY.invoke(6f), buttonWidth, screenH(0.28f)))
                .setColors(panelColor, Settings.CREAM_COLOR)
                .setAlignment(0.92f, 0.1f, true)
                .setFont(FontHelper.tipHeaderFont, 1);

        previewCards = EUIButton.createHexagonalButton(xPos, getY.invoke(8.3f), buttonWidth, buttonHeight)
                .setLabel(FontHelper.buttonLabelFont, 0.8f, PGR.core.strings.sui_showCardPool)
                .setOnClick(() -> previewCardPool(null))
                .setColor(Color.LIGHT_GRAY);

        resetPoolButton = EUIButton.createHexagonalButton(xPos, getY.invoke(9.1f), buttonWidth, buttonHeight)
                .setLabel(FontHelper.buttonLabelFont, 0.8f, PGR.core.strings.sui_resetPool)
                .setTooltip(PGR.core.strings.sui_resetPool, PGR.core.strings.sui_resetPoolDesc)
                .setOnClick(() -> this.selectAll(false))
                .setColor(Color.ROYAL);

        resetBanButton = EUIButton.createHexagonalButton(xPos, getY.invoke(9.9f), buttonWidth, buttonHeight)
                .setLabel(FontHelper.buttonLabelFont, 0.8f, PGR.core.strings.sui_resetBan)
                .setTooltip(PGR.core.strings.sui_resetBan, PGR.core.strings.sui_resetBanDesc)
                .setOnClick(this::unbanAll)
                .setColor(Color.FIREBRICK);

        cancel = EUIButton.createHexagonalButton(screenW(0.015f), getY.invoke(12.5f), buttonWidth, buttonHeight * 1.28f)
                .setText(GridCardSelectScreen.TEXT[1])
                .setOnClick(this::cancel)
                .setColor(Color.FIREBRICK);

        confirm = EUIButton.createHexagonalButton(xPos, getY.invoke(12.5f), buttonWidth, buttonHeight * 1.28f)
                .setText(GridCardSelectScreen.TEXT[0])
                .setOnClick(this::proceed)
                .setColor(Color.FOREST);

        contextMenu = (EUIContextMenu<ContextOption>) new EUIContextMenu<ContextOption>(new EUIHitbox(0, 0, 0, 0), o -> o.name)
                .setOnOpenOrClose(isOpen -> isScreenDisabled = isOpen)
                .setOnChange(options -> {
                    for (ContextOption o : options) {
                        o.onSelect.invoke(this, selectedCard);
                    }
                })
                .setCanAutosizeButton(true)
                .setItems(ContextOption.values());
    }

    public static boolean isRarityAllowed(AbstractCard.CardRarity rarity, AbstractCard.CardType type) {
        switch (rarity) {
            case COMMON:
            case UNCOMMON:
            case RARE:
                return type != AbstractCard.CardType.STATUS && type != AbstractCard.CardType.CURSE;
        }
        return false;
    }

    // Calculate the number of cards in each set, then update the loadout representative with that amount
    public void calculateCardCounts() {
        shownCards.clear();
        shownColorlessCards.clear();
        isCustom = false;

        for (ChoiceCard<PCLLoadout> entry : loadouts) {
            if (entry.value.isLocked()) {
                continue;
            }
            boolean isSelected = (entry.value.isCore() || selectedLoadouts.contains(entry.value.ID) || currentSeriesCard == entry);
            int allowedAmount = 0;
            int unlockedAmount = 0;
            for (PCLCardData data : entry.value.getCards()) {
                if (!data.isLocked()) {
                    unlockedAmount += 1;
                }
                if (!bannedCards.contains(data.ID)) {
                    if (isSelected) {
                        AbstractCard c = allCards.get(data.ID);
                        if (c != null) {
                            shownCards.add(c);
                            if (c instanceof PCLDynamicCard) {
                                isCustom = true;
                            }
                        }
                    }
                    allowedAmount += 1;
                }
            }

            boolean refreshText = false;
            PSkill<?> unlockEffect = entry.getEffect(0);
            if (unlockEffect != null) {
                unlockEffect.setAmount(unlockedAmount);
                refreshText = true;
            }
            PSkill<?> bannedEffect = entry.getEffect(1);
            if (bannedEffect != null) {
                bannedEffect.setAmount(allowedAmount);
                refreshText = true;
            }
            if (refreshText) {
                entry.cardText.forceRefresh();
            }
        }

        for (AbstractCard c : CustomCardLibraryScreen.getCards(AbstractCard.CardColor.COLORLESS)) {
            String replacement = data.resources.getReplacement(c.cardID);
            if (replacement != null) {
                c = CardLibrary.getCard(replacement);
            }
            if (isRarityAllowed(c.rarity, c.type) &&
                    data.resources.containsColorless(c) && !bannedColorless.contains(c.cardID)) {
                shownColorlessCards.add(c);
            }
        }

        // Custom colorless
        if (data.canUseCustomColorless()) {
            for (PCLCustomCardSlot slot : PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS)) {
                if (!bannedColorless.contains(slot.ID) && isRarityAllowed(slot.getFirstBuilder().cardRarity, slot.getFirstBuilder().cardType)) {
                    shownColorlessCards.add(slot.make());
                    isCustom = true;
                }
            }
        }
    }

    public void cancel() {
        SingleCardViewPopup.isViewingUpgrade = false;
        cardGrid.clear();
        close();
    }

    public void commitChanges(PCLPlayerData<?, ?, ?> data) {
        data.selectedLoadout = currentSeriesCard != null ? currentSeriesCard.value : null;
        HashSet<String> banned = new HashSet<>(bannedCards);
        banned.addAll(bannedColorless);
        data.config.bannedCards.set(banned);
        data.config.selectedLoadouts.set(new HashSet<>(selectedLoadouts));
        data.saveSelectedLoadout();

        EUIUtils.logInfoIfDebug(this, "Selected Loadout: " + data.selectedLoadout.getName());
        EUIUtils.logInfoIfDebug(this, "Series Count: " + data.config.selectedLoadouts.get().size());
        EUIUtils.logInfoIfDebug(this, "Banned Size: " + data.config.bannedCards.get().size());
    }

    public void createCards(PCLPlayerData<?, ?, ?> data) {
        allCards.clear();
        allColorlessCards.clear();
        shownCards.clear();
        shownColorlessCards.clear();
        loadouts.clear();
        bannedCards.clear();
        bannedColorless.clear();
        selectedLoadouts.clear();

        selectedLoadouts.addAll(data.config.selectedLoadouts.get());

        for (PCLLoadout series : data.getEveryLoadout()) {
            boolean isSelected = Objects.equals(series.ID, data.selectedLoadout.ID);
            // Add series representation to the grid selection
            final boolean isBeta = data.customDisablesProgression() &&
                    (series instanceof PCLCustomLoadout || series.cardDatas.isEmpty());
            final ChoiceCard<PCLLoadout> gridCard = series.buildCard(isSelected, selectedLoadouts.contains(series.ID), isBeta);
            if (gridCard != null) {
                loadouts.add(gridCard);
                gridCard.targetTransparency = 1f;

                if (isSelected) {
                    currentSeriesCard = gridCard;
                    gridCard.setCardRarity(AbstractCard.CardRarity.RARE);
                    gridCard.beginGlowing();
                }
            }
            else {
                EUIUtils.logError(this, "BuildCard() failed, " + series.getName());
            }

            // Add this series cards to the total list of available cards
            for (PCLCardData cData : series.getCards()) {
                AbstractCard card = cData.getCard();
                card.isSeen = !cData.isLocked();
                allCards.put(cData.ID, card);
                if (card instanceof PCLCard) {
                    ((PCLCard) card).affinities.updateSortedList();
                }
                if (data.config.bannedCards.get().contains(cData.ID)) {
                    bannedCards.add(cData.ID);
                }
            }

            // Colorless bans
            for (PCLCardData cData : series.getColorlessCards()) {
                AbstractCard card = cData.getCard();
                allColorlessCards.put(cData.ID, card);
                if (card instanceof PCLCard) {
                    ((PCLCard) card).affinities.updateSortedList();
                }
                if (data.config.bannedCards.get().contains(cData.ID)) {
                    bannedColorless.add(cData.ID);
                }
            }
        }
        calculateCardCounts();
    }

    @Override
    public void dispose() {
        super.dispose();

        SingleCardViewPopup.isViewingUpgrade = false;

        if (onClose != null) {
            onClose.invoke();
        }
    }

    public void forceUpdateText() {
        calculateCardCounts();
        totalCardsCache = shownCards.size();
        totalColorlessCache = shownColorlessCards.size();
        totalCardsChanged(totalCardsCache, totalColorlessCache);
    }

    // Grid will not actually contain the core series card for now
    public Collection<AbstractCard> getAllCards() {
        return loadouts
                .stream()
                .filter(entry -> !entry.value.isCore())
                .sorted((a, b) -> {
                    PCLLoadout lA = a.value;
                    PCLLoadout lB = b.value;
                    boolean lACustom = lA.isOnlyCustom();
                    boolean lBCustom = lB.isOnlyCustom();
                    if (lACustom && !lBCustom) {
                        return 1;
                    }
                    else if (lBCustom && !lACustom) {
                        return -1;
                    }

                    if (lA.unlockLevel != lB.unlockLevel) {
                        return lA.unlockLevel - lB.unlockLevel;
                    }
                    return StringUtils.compare(a.name, b.name);
                })
                .collect(Collectors.toList());
    }

    public ArrayList<AbstractRelic> getAvailableRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>(GameUtilities.getRelics(data.resources.cardColor).values());

        // Get additional relics that this character can use
        String[] additional = data.getAdditionalRelicIDs(false);
        if (additional != null) {
            for (String id : additional) {
                relics.add(RelicLibrary.getRelic(id));
            }
        }

        // Get other non base-game relics
        for (AbstractRelic r : GameUtilities.getRelics(AbstractCard.CardColor.COLORLESS).values()) {
            if (EUIGameUtils.getModInfo(r) != null && GameUtilities.isRelicTierSpawnable(r.tier)) {
                relics.add(r);
            }
        }

        relics.removeIf(r -> {
            if (UnlockTracker.isRelicLocked(r.relicId)) {
                return true;
            }
            for (ChoiceCard<PCLLoadout> entry : loadouts) {
                if (entry.value.isRelicFromLoadout(r.relicId) && (entry.value.isLocked() || (!selectedLoadouts.contains(entry.value.ID) && currentSeriesCard != entry))) {
                    return true;
                }
                else if (entry.value.getStartingRelics().contains(r.relicId)) {
                    return true;
                }
            }
            return false;
        });

        return relics;
    }

    public ArrayList<AbstractCard> getCardPool(PCLLoadout loadout) {
        final ArrayList<AbstractCard> cards = new ArrayList<>();
        if (loadout != null) {
            for (PCLCardData data : loadout.getCards()) {
                cards.add(allCards.get(data.ID));
            }
        }
        else {
            cards.addAll(shownCards);
        }

        cards.sort(CardKeywordFilters::rankByName);
        cards.sort(CardKeywordFilters::rankByRarity);
        return cards;
    }

    public ArrayList<AbstractCard> getColorlessPool() {
        final ArrayList<AbstractCard> cards = new ArrayList<>();
        if (data != null) {
            for (AbstractCard c : CustomCardLibraryScreen.getCards(AbstractCard.CardColor.COLORLESS)) {
                String replacement = data.resources.getReplacement(c.cardID);
                if (replacement != null) {
                    c = CardLibrary.getCard(replacement);
                }
                if (isRarityAllowed(c.rarity, c.type) &&
                        data.resources.containsColorless(c)) {
                    cards.add(c.makeCopy());
                }
            }

            // Custom colorless
            if (data.canUseCustomColorless()) {
                for (PCLCustomCardSlot slot : PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS)) {
                    if (isRarityAllowed(slot.getFirstBuilder().cardRarity, slot.getFirstBuilder().cardType)) {
                        cards.add(slot.make());
                    }
                }
            }
        }
        else {
            for (AbstractCard c : CustomCardLibraryScreen.getCards(AbstractCard.CardColor.COLORLESS)) {
                for (PCLResources<?, ?, ?, ?> resources : PGR.getRegisteredResources()) {
                    if (isRarityAllowed(c.rarity, c.type) && !resources.filterColorless(c)) {
                        cards.add(c.makeCopy());
                    }
                }
            }
        }

        cards.sort(CardKeywordFilters::rankByName);
        cards.sort(CardKeywordFilters::rankByRarity);
        return cards;
    }

    public boolean isValid() {
        return shownCards.size() >= data.minimumCards && shownColorlessCards.size() >= data.minimumColorless;
    }

    // Since core sets cannot be toggled, only show the view card option for them
    public void onCardRightClicked(AbstractCard card) {
        selectedCard = (ChoiceCard<PCLLoadout>) card;
        if (!selectedCard.value.isLocked()) {
            contextMenu.setItems(ContextOption.getOptions(card.type != PCLLoadout.SELECTABLE_TYPE ? null : selectedLoadouts.contains(selectedCard.value.ID)));
            contextMenu.positionToOpen();
        }
    }

    public void open(CharacterOption characterOption, PCLPlayerData<?, ?, ?> data, ActionT0 onClose) {
        super.open();

        this.onClose = onClose;
        this.characterOption = characterOption;
        this.data = data;
        EUI.actingColor = data.resources.cardColor;

        createCards(data);
        cardGrid.add(getAllCards());

        EUI.countingPanel.open(shownCards, data.resources.cardColor, false, false);

        EUITourTooltip.queueFirstView(PGR.config.tourSeriesSelect,
                new EUITourTooltip(cardGrid.group.group.get(0).hb, PGR.core.strings.csel_seriesEditor, PGR.core.strings.sui_instructions1)
                        .setPosition(Settings.WIDTH * 0.25f, Settings.HEIGHT * 0.75f),
                new EUITourTooltip(cardGrid.group.group.get(0).hb, PGR.core.strings.csel_seriesEditor, PGR.core.strings.sui_instructions2)
                        .setPosition(Settings.WIDTH * 0.25f, Settings.HEIGHT * 0.75f),
                new EUITourTooltip(statsPanel.hb, PGR.core.strings.csel_seriesEditor, PGR.core.strings.sui_totalInstructions)
                        .setPosition(Settings.WIDTH * 0.6f, Settings.HEIGHT * 0.75f),
                loadoutEditor.makeTour(true),
                colorlessButton.makeTour(true),
                relicsButton.makeTour(true)
        );
    }

    protected void openLoadoutEditor() {
        if (characterOption != null && currentSeriesCard != null && data != null) {
            proceed();
            PGR.loadoutEditor.open(currentSeriesCard.value, data, characterOption, this.onClose);
        }
    }

    public void previewCardPool(ChoiceCard<PCLLoadout> source) {
        if (!shownCards.isEmpty()) {
            PCLLoadout loadout = null;
            if (source != null) {
                source.unhover();
                loadout = source.value;
            }
            previewCards(getCardPool(loadout), loadout);
        }
    }

    // Core loadout cards cannot be toggled off
    public void previewCards(ArrayList<AbstractCard> cards, PCLLoadout loadout) {
        currentEffect = new ViewInGameCardPoolEffect(cards, bannedCards, this::forceUpdateText)
                .setCanToggle((loadout != null && !loadout.isCore()) || data.canEditCore())
                .setStartingPosition(InputHelper.mX, InputHelper.mY)
                .addCallback(effect -> {
                    // Only allow effect to affect the subset of cards shown in the effect
                    for (AbstractCard c : cards) {
                        if (effect.bannedCards.contains(c.cardID)) {
                            bannedCards.add(c.cardID);
                        }
                        else {
                            bannedCards.remove(c.cardID);
                        }
                    }
                    forceUpdateText();
                });
    }

    public void previewColorless() {
        ArrayList<AbstractCard> cPool = getColorlessPool();
        currentEffect = new ViewInGameCardPoolEffect(cPool, bannedColorless, this::forceUpdateText)
                .setStartingPosition(InputHelper.mX, InputHelper.mY)
                .addCallback(effect -> {
                    // Only allow effect to affect the subset of cards shown in the effect
                    for (AbstractCard c : cPool) {
                        if (effect.bannedCards.contains(c.cardID)) {
                            bannedColorless.add(c.cardID);
                        }
                        else {
                            bannedColorless.remove(c.cardID);
                        }
                    }
                    forceUpdateText();
                });
    }

    protected void previewRelics() {
        HashSet<String> res = new HashSet<>(data.config.bannedRelics.get());
        ArrayList<AbstractRelic> rPool = getAvailableRelics();
        currentEffect = new ViewInGameRelicPoolEffect(getAvailableRelics(), new HashSet<>(data.config.bannedRelics.get()))
                .addCallback((effect) -> {
                    // Only allow effect to affect the subset of cards shown in the effect
                    for (AbstractRelic c : rPool) {
                        if (effect.bannedRelics.contains(c.relicId)) {
                            res.add(c.relicId);
                        }
                        else {
                            res.remove(c.relicId);
                        }
                    }
                    data.config.bannedRelics.set(res);
                    forceUpdateText();
                });
    }

    public void proceed() {
        SingleCardViewPopup.isViewingUpgrade = false;
        cardGrid.clear();
        commitChanges(data);
        close();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        cardGrid.tryRender(sb);

        loadoutEditor.tryRender(sb);
        colorlessButton.renderImpl(sb);
        relicsButton.renderImpl(sb);
        resetPoolButton.tryRender(sb);
        resetBanButton.tryRender(sb);
        previewCards.renderImpl(sb);
        cancel.renderImpl(sb);
        confirm.renderImpl(sb);

        previewCardsInfo.renderImpl(sb);
        statsPanel.renderImpl(sb);

        if (currentEffect != null) {
            currentEffect.render(sb);
        }
        else {
            EUI.countingPanel.tryRender(sb);
        }

        contextMenu.tryRender(sb);
    }

    public void selectAll(boolean value) {
        for (ChoiceCard<PCLLoadout> c : loadouts) {
            toggleCards(c.value, value);
        }
        updateGlows();
        calculateCardCounts();
    }

    // You cannot select core loadout cards
    public void selectCard(AbstractCard card) {
        if (!isScreenDisabled && card instanceof ChoiceCard && loadouts.contains(card) && card.type == PCLLoadout.SELECTABLE_TYPE) {
            currentSeriesCard = (ChoiceCard<PCLLoadout>) card;
            updateGlows();
            calculateCardCounts();
        }
    }

    public void toggleCards(PCLLoadout loadout, boolean value) {
        if (value) {
            selectedLoadouts.add(loadout.ID);
        }
        else {
            selectedLoadouts.remove(loadout.ID);
        }
        updateGlows();
        calculateCardCounts();
    }

    protected void totalCardsChanged(int totalCards, int totalColorless) {
        if (EUI.countingPanel.isActive) {
            EUI.countingPanel.open(shownCards, data.resources.cardColor, false, false);
        }

        PCLLoadout cur = currentSeriesCard != null ? currentSeriesCard.value : null;
        String seriesLabel = PGR.core.strings.csel_leftText + EUIUtils.SPLIT_LINE + PCLCoreStrings.colorString("g", (currentSeriesCard != null) ? currentSeriesCard.name : "");
        String totalLabel = PGR.core.strings.sui_totalCards(
                cur != null && !selectedLoadouts.contains(cur.ID) ? 1 + selectedLoadouts.size() : selectedLoadouts.size(),
                totalCards >= data.minimumCards ? "g" : "r",
                totalCards,
                data.minimumCards,
                totalColorless >= data.minimumColorless ? "g" : "r",
                totalColorless,
                data.minimumColorless);
        statsPanel.setLabel(
                isCustom ? EUIUtils.joinStrings(EUIUtils.DOUBLE_SPLIT_LINE, seriesLabel, totalLabel, PCLCoreStrings.colorString("r", PGR.core.strings.csel_betaCardSet))
                        : EUIUtils.joinStrings(EUIUtils.DOUBLE_SPLIT_LINE, seriesLabel, totalLabel)
        );

        confirm.setInteractable(isValid());
    }

    public void unbanAll() {
        bannedCards.clear();
        bannedColorless.clear();
        calculateCardCounts();
    }

    public void unbanCards(PCLLoadout loadout, boolean value) {
        Collection<String> cardIds = EUIUtils.map(loadout.getCards(), l -> l.ID);
        if (value) {
            cardIds.forEach(bannedCards::remove);
        }
        else {
            bannedCards.addAll(cardIds);
        }
        calculateCardCounts();
    }

    protected void updateGlows() {
        for (ChoiceCard<PCLLoadout> c : loadouts) {
            c.stopGlowing();
            if (c == currentSeriesCard) {
                currentSeriesCard.setCardRarity(AbstractCard.CardRarity.RARE);
                currentSeriesCard.color = data.resources.cardColor;
                currentSeriesCard.beginGlowing();
            }
            else {
                c.stopGlowing();
                if (c.type == PCLLoadout.SELECTABLE_TYPE) {
                    if (selectedLoadouts.contains(c.value.ID)) {
                        c.setCardRarity(AbstractCard.CardRarity.UNCOMMON);
                        c.color = data.resources.cardColor;
                    }
                    else {
                        c.setCardRarity(AbstractCard.CardRarity.COMMON);
                        c.color = AbstractCard.CardColor.COLORLESS;
                    }
                }
            }
        }
    }

    @Override
    public void updateImpl() {
        CardCrawlGame.mainMenuScreen.screenColor.a = MathHelper.popLerpSnap(CardCrawlGame.mainMenuScreen.screenColor.a, 0.8F);

        EUI.countingPanel.tryUpdate();

        if (currentEffect != null) {
            currentEffect.update();

            if (currentEffect.isDone) {
                currentEffect = null;
            }
            else {
                return;
            }
        }
        else {
            super.updateImpl();
        }

        if (totalCardsCache != shownCards.size() || totalColorlessCache != shownColorlessCards.size()) {
            totalCardsCache = shownCards.size();
            totalColorlessCache = shownColorlessCards.size();
            totalCardsChanged(totalCardsCache, totalColorlessCache);
        }

        loadoutEditor.tryUpdate();
        colorlessButton.tryUpdate();
        relicsButton.tryUpdate();

        if (!isScreenDisabled) {
            resetPoolButton.tryUpdate();
            resetBanButton.tryUpdate();
            previewCards.updateImpl();
            cancel.updateImpl();
            confirm.updateImpl();
            cardGrid.tryUpdate();
            previewCardsInfo.tryUpdate();
            statsPanel.tryUpdate();
            statsPanel.tryUpdate();
        }

        contextMenu.tryUpdate();
    }

    public enum ContextOption {
        Deselect(PGR.core.strings.sui_removeFromPool, (s, c) -> s.toggleCards(c.value, false)),
        Select(PGR.core.strings.sui_addToPool, (s, c) -> s.toggleCards(c.value, true)),
        UnbanAll(PGR.core.strings.sui_resetBan, (s, c) -> s.unbanCards(c.value, true)),
        ViewCards(PGR.core.strings.sui_viewPool, (screen, card) -> {
            if (screen.currentEffect == null) {
                screen.previewCardPool(card);
            }
        });

        public final String name;
        public final ActionT2<PCLSeriesSelectScreen, ChoiceCard<PCLLoadout>> onSelect;

        ContextOption(String name, ActionT2<PCLSeriesSelectScreen, ChoiceCard<PCLLoadout>> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }

        public static Collection<ContextOption> getOptions(Boolean isSelected) {
            if (isSelected == null) {
                return Collections.singleton(ViewCards);
            }
            return !isSelected ? Arrays.asList(Select, UnbanAll, ViewCards) : Arrays.asList(Deselect, UnbanAll, ViewCards);
        }
    }

}
