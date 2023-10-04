package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
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
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.screens.CustomCardLibraryScreen;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.screen.ViewInGameCardPoolEffect;
import pinacolada.effects.screen.ViewInGameRelicPoolEffect;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.utilities.GameUtilities;

import java.util.*;
import java.util.stream.Collectors;

// Copied and modified from STS-AnimatorMod
public class PCLSeriesSelectScreen extends AbstractMenuScreen {
    public static final int MINIMUM_CARDS = 75; // 75
    public static final int MINIMUM_COLORLESS = 40;
    public final EUICardGrid cardGrid;
    public final EUILabel startingDeck;
    public final EUIButton resetPoolButton;
    public final EUIButton resetBanButton;
    public final EUIButton previewCards;
    public final EUIButton colorlessButton;
    public final EUIButton cancel;
    public final EUIButton confirm;
    public final EUIButton loadoutEditor;
    public final EUIButton relicsButton;
    public final EUITextBox typesAmount;
    public final EUITextBox previewCardsInfo;
    public final EUIContextMenu<ContextOption> contextMenu;
    public final ArrayList<AbstractCard> shownCards = new ArrayList<>();
    public final ArrayList<AbstractCard> shownColorlessCards = new ArrayList<>();
    public final HashMap<String, AbstractCard> allCards = new HashMap<>();
    public final HashMap<String, AbstractCard> allColorlessCards = new HashMap<>();
    public final HashMap<PCLCard, PCLLoadout> loadoutMap = new HashMap<>();
    public final HashSet<String> bannedCards = new HashSet<>();
    public final HashSet<String> bannedColorless = new HashSet<>();
    public final HashSet<String> selectedLoadouts = new HashSet<>();
    protected AbstractPlayerData<?, ?> data;
    protected ActionT0 onClose;
    protected CharacterOption characterOption;
    protected PCLCard selectedCard;
    protected PCLEffect currentEffect;
    protected int totalCardsCache = 0;
    protected int totalColorlessCache = 0;
    public PCLCard currentSeriesCard;
    public boolean isScreenDisabled;

    public PCLSeriesSelectScreen() {
        final Texture panelTexture = EUIRM.images.panelRounded.texture();
        final FuncT1<Float, Float> getY = (delta) -> screenH(0.95f) - screenH(0.07f * delta);
        final float buttonHeight = screenH(0.05f);
        final float buttonWidth = screenW(0.18f);
        final float xPos = screenW(0.82f);

        cardGrid = (EUICardGrid) new EUICardGrid(0.3f, false)
                .setOnClick(this::onCardClicked)
                .setOnRightClick(this::onCardRightClicked)
                .showScrollbar(false);

        startingDeck = new EUILabel(null, new EUIHitbox(screenW(0.18f), screenH(0.05f))
                .setCenter(screenW(0.08f), screenH(0.97f)))
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
                .setSmartText(true)
                .setColor(Settings.CREAM_COLOR);

        loadoutEditor = new EUIButton(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(startingDeck.hb.x + scale(30), startingDeck.hb.y - scale(65), scale(150), scale(52)))
                .setTooltip(PGR.core.strings.csel_deckEditor, PGR.core.strings.csel_deckEditorInfo)
                .setLabel(EUIFontHelper.cardDescriptionFontNormal, 0.9f, PGR.core.strings.csel_deckEditor)
                .setColor(new Color(0.3f, 0.5f, 0.8f, 1))
                .setOnClick(this::openLoadoutEditor);

        colorlessButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(loadoutEditor.hb.x, loadoutEditor.hb.y - scale(65), scale(150), scale(52)))
                .setTooltip(PGR.core.strings.sui_showColorless, PGR.core.strings.sui_showColorlessInfo)
                .setLabel(EUIFontHelper.cardDescriptionFontNormal, 0.9f, PGR.core.strings.sui_showColorless)
                .setColor(new Color(0.5f, 0.3f, 0.8f, 1))
                .setOnClick(this::previewColorless);

        relicsButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(colorlessButton.hb.x, colorlessButton.hb.y - scale(65), scale(150), scale(52)))
                .setTooltip(PGR.core.strings.sui_relicPool, PGR.core.strings.sui_relicPoolInfo)
                .setLabel(EUIFontHelper.cardDescriptionFontNormal, 0.9f, PGR.core.strings.sui_relicPool)
                .setColor(new Color(0.8f, 0.3f, 0.5f, 1))
                .setOnClick(this::previewRelics);

        Color panelColor = new Color(0.08f, 0.08f, 0.08f, 1);
        previewCardsInfo = new EUITextBox(panelTexture, new EUIHitbox(xPos, getY.invoke(2.5f), buttonWidth, screenH(0.15f)))
                .setLabel(EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, PGR.core.strings.sui_instructions1, PGR.core.strings.sui_instructions2))
                .setAlignment(0.85f, 0.1f, true)
                .setColors(panelColor, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 1f);
        typesAmount = new EUITextBox(panelTexture, new EUIHitbox(xPos, getY.invoke(4f), buttonWidth, screenH(0.12f)))
                .setColors(panelColor, Settings.GOLD_COLOR)
                .setAlignment(0.82f, 0.1f, true)
                .setFont(EUIFontHelper.cardTipTitleFontBase, 1);

        previewCards = EUIButton.createHexagonalButton(xPos, getY.invoke(6.3f), buttonWidth, buttonHeight)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, PGR.core.strings.sui_showCardPool)
                .setOnClick(() -> previewCardPool(null))
                .setColor(Color.LIGHT_GRAY);

        resetPoolButton = EUIButton.createHexagonalButton(xPos, getY.invoke(7.1f), buttonWidth, buttonHeight)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, PGR.core.strings.sui_resetPool)
                .setTooltip(PGR.core.strings.sui_resetPool, PGR.core.strings.sui_resetPoolDesc)
                .setOnClick(() -> this.selectAll(false))
                .setColor(Color.ROYAL);

        resetBanButton = EUIButton.createHexagonalButton(xPos, getY.invoke(7.9f), buttonWidth, buttonHeight)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, PGR.core.strings.sui_resetBan)
                .setTooltip(PGR.core.strings.sui_resetBan, PGR.core.strings.sui_resetBanDesc)
                .setOnClick(this::unbanAll)
                .setColor(Color.FIREBRICK);

        cancel = EUIButton.createHexagonalButton(xPos, getY.invoke(12f), buttonWidth, buttonHeight * 1.25f)
                .setText(GridCardSelectScreen.TEXT[1])
                .setOnClick(this::cancel)
                .setColor(Color.FIREBRICK);

        confirm = EUIButton.createHexagonalButton(xPos, getY.invoke(13f), buttonWidth, buttonHeight * 1.25f)
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

        for (Map.Entry<PCLCard, PCLLoadout> entry : loadoutMap.entrySet()) {
            PCLLoadout loadout = entry.getValue();
            if (loadout.isLocked()) {
                continue;
            }
            boolean isSelected = (loadout.isCore() || selectedLoadouts.contains(entry.getValue().ID) || currentSeriesCard == entry.getKey());
            int allowedAmount = 0;
            int unlockedAmount = 0;
            for (PCLCardData data : loadout.cardDatas) {
                if (!data.isLocked()) {
                    unlockedAmount += 1;
                }
                if (!bannedCards.contains(data.ID)) {
                    if (isSelected) {
                        AbstractCard c = allCards.get(data.ID);
                        if (c != null) {
                            shownCards.add(c);
                        }
                    }
                    allowedAmount += 1;
                }
            }

            PSkill<?> unlockEffect = entry.getKey().getEffect(0);
            if (unlockEffect != null) {
                unlockEffect.setAmount(unlockedAmount);
            }
            PSkill<?> bannedEffect = entry.getKey().getEffect(1);
            if (bannedEffect != null) {
                bannedEffect.setAmount(allowedAmount);
            }
        }

        for (AbstractCard c : CustomCardLibraryScreen.getCards(AbstractCard.CardColor.COLORLESS)) {
            if (isRarityAllowed(c.rarity, c.type) &&
                    data.resources.containsColorless(c) && !bannedColorless.contains(c.cardID)) {
                shownColorlessCards.add(c);
            }
        }

        // Add additional cards
        String[] additional = data.getAdditionalCardIDs();
        if (additional != null) {
            for (String s : additional) {
                AbstractCard c = CardLibrary.getCard(s);
                if (c != null) {
                    if (GameUtilities.isColorlessCardColor(c.color)) {
                        shownColorlessCards.add(c);
                    }
                    else {
                        shownCards.add(c);
                    }
                }
            }
        }
    }

    public void cancel() {
        SingleCardViewPopup.isViewingUpgrade = false;
        cardGrid.clear();
        close();
    }

    public void chooseSeries(AbstractCard card) {
        if (selectCard(EUIUtils.safeCast(card, PCLCard.class))) {
            updateStartingDeckText();
        }
    }

    public void commitChanges(AbstractPlayerData<?, ?> data) {
        data.selectedLoadout = find(currentSeriesCard);
        HashSet<String> banned = new HashSet<>(bannedCards);
        banned.addAll(bannedColorless);
        // TODO add list config class that lets you set the items of a config without replacing its list
        data.config.bannedCards.set(banned);
        data.config.selectedLoadouts.set(new HashSet<>(selectedLoadouts));
        data.saveSelectedLoadout();

        EUIUtils.logInfoIfDebug(this, "Selected Loadout: " + data.selectedLoadout.getName());
        EUIUtils.logInfoIfDebug(this, "Series Count: " + data.config.selectedLoadouts.get().size());
        EUIUtils.logInfoIfDebug(this, "Banned Size: " + data.config.bannedCards.get().size());
    }

    public void createCards(AbstractPlayerData<?, ?> data) {
        allCards.clear();
        allColorlessCards.clear();
        shownCards.clear();
        shownColorlessCards.clear();
        loadoutMap.clear();
        bannedCards.clear();
        bannedColorless.clear();
        selectedLoadouts.clear();

        selectedLoadouts.addAll(data.config.selectedLoadouts.get());

        for (PCLLoadout series : data.getEveryLoadout()) {
            boolean isSelected = Objects.equals(series.ID, data.selectedLoadout.ID);
            // Add series representation to the grid selection
            final PCLCard gridCard = series.buildCard(isSelected, selectedLoadouts.contains(series.ID));
            if (gridCard != null) {
                loadoutMap.put(gridCard, series);
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
            for (PCLCardData cData : series.cardDatas) {
                AbstractCard card = cData.makeCardFromLibrary(0);
                allCards.put(cData.ID, card);
                if (card instanceof PCLCard) {
                    ((PCLCard) card).affinities.updateSortedList();
                }
                if (data.config.bannedCards.get().contains(cData.ID)) {
                    bannedCards.add(cData.ID);
                }
            }

            // Colorless bans
            for (PCLCardData cData : series.colorlessData) {
                AbstractCard card = cData.makeCardFromLibrary(0);
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

        toggleViewUpgrades(false);

        if (onClose != null) {
            onClose.invoke();
        }
    }

    public PCLLoadout find(PCLCard card) {
        return loadoutMap.get(card);
    }

    public void forceUpdateText() {
        calculateCardCounts();
        totalCardsCache = shownCards.size();
        totalColorlessCache = shownColorlessCards.size();
        totalCardsChanged(totalCardsCache, totalColorlessCache);
    }

    // Grid will not actually contain the core series card for now
    public Collection<AbstractCard> getAllCards() {
        return loadoutMap.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isCore())
                .sorted((a, b) -> {
                    PCLLoadout lA = a.getValue();
                    PCLLoadout lB = b.getValue();
                    if (lA.unlockLevel != lB.unlockLevel) {
                        return lA.unlockLevel - lB.unlockLevel;
                    }
                    return StringUtils.compare(a.getKey().name, b.getKey().name);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Collection<PCLLoadout> getAllLoadouts() {
        return loadoutMap.values();
    }

    public ArrayList<AbstractRelic> getAvailableRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>(GameUtilities.getRelics(data.resources.cardColor).values());

        // Get additional relics that this character can use
        String[] additional = data.getAdditionalRelicIDs();
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
            for (Map.Entry<PCLCard, PCLLoadout> entry : loadoutMap.entrySet()) {
                PCLLoadout loadout = entry.getValue();
                if (loadout.isRelicFromLoadout(r.relicId) && (loadout.isLocked() || (!selectedLoadouts.contains(loadout.ID) && currentSeriesCard != entry.getKey()))) {
                    return true;
                }
                else if (loadout.getStartingRelics().contains(r.relicId)) {
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
            for (PCLCardData data : loadout.cardDatas) {
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
                if (isRarityAllowed(c.rarity, c.type) &&
                        data.resources.containsColorless(c)) {
                    cards.add(c.makeCopy());
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
        return shownCards.size() >= PCLSeriesSelectScreen.MINIMUM_CARDS && shownColorlessCards.size() >= PCLSeriesSelectScreen.MINIMUM_COLORLESS;
    }

    protected void onCardClicked(AbstractCard card) {
        if (!isScreenDisabled) {
            chooseSeries(card);
        }
    }

    // Since core sets cannot be toggled, only show the view card option for them
    public void onCardRightClicked(AbstractCard card) {
        selectedCard = EUIUtils.safeCast(card, PCLCard.class);
        PCLLoadout c = find(selectedCard);
        if (!c.isLocked()) {
            contextMenu.setItems(ContextOption.getOptions(card.type != PCLLoadout.SELECTABLE_TYPE ? null : selectedLoadouts.contains(c.ID)));
            contextMenu.positionToOpen();
        }
    }

    public void open(CharacterOption characterOption, AbstractPlayerData<?, ?> data, ActionT0 onClose) {
        super.open();

        this.onClose = onClose;
        this.characterOption = characterOption;
        this.data = data;
        EUI.actingColor = data.resources.cardColor;

        createCards(data);
        cardGrid.add(getAllCards());
        updateStartingDeckText();

        EUI.countingPanel.open(shownCards, data.resources.cardColor, false);

        EUITourTooltip.queueFirstView(PGR.config.tourSeriesSelect,
                new EUITourTooltip(cardGrid.group.group.get(0).hb, PGR.core.strings.csel_seriesEditor, PGR.core.strings.sui_instructions1)
                        .setPosition(Settings.WIDTH * 0.25f, Settings.HEIGHT * 0.75f),
                new EUITourTooltip(cardGrid.group.group.get(0).hb, PGR.core.strings.csel_seriesEditor, PGR.core.strings.sui_instructions2)
                        .setPosition(Settings.WIDTH * 0.25f, Settings.HEIGHT * 0.75f),
                new EUITourTooltip(typesAmount.hb, PGR.core.strings.csel_seriesEditor, PGR.core.strings.sui_totalInstructions)
                        .setPosition(Settings.WIDTH * 0.6f, Settings.HEIGHT * 0.75f),
                loadoutEditor.makeTour(true),
                colorlessButton.makeTour(true),
                relicsButton.makeTour(true)
        );
    }

    protected void openLoadoutEditor() {
        PCLLoadout current = find(currentSeriesCard);
        if (characterOption != null && current != null && data != null) {
            proceed();
            PGR.loadoutEditor.open(current, data, characterOption, this.onClose);
        }
    }

    public void previewCardPool(AbstractCard source) {
        if (shownCards.size() > 0) {
            PCLLoadout loadout = null;
            if (source != null) {
                source.unhover();
                loadout = find(EUIUtils.safeCast(source, PCLCard.class));
            }
            previewCards(getCardPool(loadout), loadout);
        }
    }

    // Core loadout cards cannot be toggled off
    public void previewCards(ArrayList<AbstractCard> cards, PCLLoadout loadout) {
        currentEffect = new ViewInGameCardPoolEffect(cards, bannedCards, this::forceUpdateText)
                .setCanToggle(loadout != null && !loadout.isCore())
                .setStartingPosition(InputHelper.mX, InputHelper.mY);
    }

    public void previewColorless() {
        currentEffect = new ViewInGameCardPoolEffect(getColorlessPool(), bannedColorless, this::forceUpdateText)
                .setStartingPosition(InputHelper.mX, InputHelper.mY);
    }

    protected void previewRelics() {
        currentEffect = new ViewInGameRelicPoolEffect(getAvailableRelics(), new HashSet<>(data.config.bannedRelics.get()))
                .addCallback((effect) -> {
                    data.config.bannedRelics.set(effect.bannedRelics);
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

        startingDeck.tryRender(sb);
        loadoutEditor.tryRender(sb);
        colorlessButton.renderImpl(sb);
        relicsButton.renderImpl(sb);
        resetPoolButton.tryRender(sb);
        resetBanButton.tryRender(sb);
        previewCards.renderImpl(sb);
        cancel.renderImpl(sb);
        confirm.renderImpl(sb);

        previewCardsInfo.renderImpl(sb);
        typesAmount.renderImpl(sb);

        if (currentEffect != null) {
            currentEffect.render(sb);
        }
        else {
            EUI.countingPanel.tryRender(sb);
        }

        contextMenu.tryRender(sb);
    }

    public void selectAll(boolean value) {
        for (PCLLoadout c : getAllLoadouts()) {
            toggleCards(c, value);
        }
        updateGlows();
        calculateCardCounts();
    }

    // You cannot select core loadout cards
    public boolean selectCard(PCLCard card) {
        if (loadoutMap.containsKey(card) && card.type == PCLLoadout.SELECTABLE_TYPE) {
            currentSeriesCard = card;
            updateGlows();
            calculateCardCounts();
            return true;
        }
        return false;
    }

    public void toggleCards(PCLLoadout loadout, boolean value) {
        if (value) {
            selectedLoadouts.add(loadout.ID);
        }
        else {
            selectedLoadouts.remove(loadout.ID);
        }
    }

    public void toggleCards(AbstractCard card, boolean value) {
        toggleCards(find(EUIUtils.safeCast(card, PCLCard.class)), value);
        updateGlows();
        calculateCardCounts();
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
    }

    protected void totalCardsChanged(int totalCards, int totalColorless) {
        if (EUI.countingPanel.isActive) {
            EUI.countingPanel.open(shownCards, data.resources.cardColor, false);
        }

        PCLLoadout cur = loadoutMap.get(currentSeriesCard);
        typesAmount.setLabel(PGR.core.strings.sui_totalCards(
                cur != null && !selectedLoadouts.contains(cur.ID) ? 1 + selectedLoadouts.size() : selectedLoadouts.size(),
                totalCards >= MINIMUM_CARDS ? "g" : "r",
                totalCards,
                MINIMUM_CARDS,
                totalColorless >= MINIMUM_COLORLESS ? "g" : "r",
                totalColorless,
                MINIMUM_COLORLESS));

        confirm.setInteractable(isValid());
    }

    public void unbanAll() {
        bannedCards.clear();
        calculateCardCounts();
    }

    public void unbanCards(AbstractCard card, boolean value) {
        unbanCards(find(EUIUtils.safeCast(card, PCLCard.class)), value);
    }

    public void unbanCards(PCLLoadout loadout, boolean value) {
        Collection<String> cardIds = EUIUtils.map(loadout.cardDatas, l -> l.ID);
        if (value) {
            cardIds.forEach(bannedCards::remove);
        }
        else {
            bannedCards.addAll(cardIds);
        }
        calculateCardCounts();
    }

    protected void updateGlows() {
        for (Map.Entry<PCLCard, PCLLoadout> entry : loadoutMap.entrySet()) {
            PCLCard c = entry.getKey();
            c.stopGlowing();
            if (c == currentSeriesCard) {
                currentSeriesCard.setCardRarity(AbstractCard.CardRarity.RARE);
                currentSeriesCard.color = data.resources.cardColor;
                currentSeriesCard.beginGlowing();
            }
            else {
                c.stopGlowing();
                if (c.type == PCLLoadout.SELECTABLE_TYPE) {
                    if (selectedLoadouts.contains(entry.getValue().ID)) {
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

        startingDeck.tryUpdate();
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
            typesAmount.tryUpdate();
        }

        contextMenu.tryUpdate();
    }

    protected void updateStartingDeckText() {
        startingDeck.setLabel(PGR.core.strings.csel_leftText + EUIUtils.SPLIT_LINE + PCLCoreStrings.colorString("y", (currentSeriesCard != null) ? currentSeriesCard.name : ""));
    }

    public enum ContextOption {
        Deselect(PGR.core.strings.sui_removeFromPool, (s, c) -> s.toggleCards(c, false)),
        Select(PGR.core.strings.sui_addToPool, (s, c) -> s.toggleCards(c, true)),
        UnbanAll(PGR.core.strings.sui_resetBan, (s, c) -> s.unbanCards(c, true)),
        ViewCards(PGR.core.strings.sui_viewPool, (screen, card) -> {
            if (screen.currentEffect == null) {
                screen.previewCardPool(card);
            }
        });

        public final String name;
        public final ActionT2<PCLSeriesSelectScreen, AbstractCard> onSelect;

        ContextOption(String name, ActionT2<PCLSeriesSelectScreen, AbstractCard> onSelect) {
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
