package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.screens.CustomCardLibraryScreen;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.screen.ViewInGameCardPoolEffect;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreStrings;

import static pinacolada.ui.characterSelection.PCLLoadoutsContainer.MINIMUM_CARDS;
import static pinacolada.ui.characterSelection.PCLLoadoutsContainer.MINIMUM_COLORLESS;

// Copied and modified from STS-AnimatorMod
public class PCLSeriesSelectScreen extends AbstractMenuScreen {
    public final PCLLoadoutsContainer container = new PCLLoadoutsContainer();
    public final EUICardGrid cardGrid;
    public final EUILabel startingDeck;
    public final EUIButton selectAllButton;
    public final EUIButton deselectAllButton;
    public final EUIButton previewCards;
    public final EUIButton colorless;
    public final EUIButton cancel;
    public final EUIButton confirm;
    public final EUIButton loadoutEditor;
    public final EUITextBox typesAmount;
    public final EUITextBox previewCardsInfo;
    public final EUIContextMenu<ContextOption> contextMenu;
    protected PCLCard selectedCard;
    protected ActionT0 onClose;
    protected ViewInGameCardPoolEffect previewCardsEffect;
    protected CharacterOption characterOption;
    protected AbstractPlayerData<?, ?> data;
    protected int totalCardsCache = 0;
    protected int totalColorlessCache = 0;
    public boolean isScreenDisabled;

    public PCLSeriesSelectScreen() {
        final Texture panelTexture = EUIRM.images.panelRounded.texture();
        final FuncT1<Float, Float> getY = (delta) -> screenH(0.95f) - screenH(0.07f * delta);
        final float buttonHeight = screenH(0.05f);
        final float buttonWidth = screenW(0.18f);
        final float xPos = screenW(0.82f);

        cardGrid = new EUICardGrid(0.41f, false)
                .setOnCardClick(this::onCardClicked)
                .setOnCardRightClick(this::onCardRightClicked)
                .showScrollbar(false);

        startingDeck = new EUILabel(null, new EUIHitbox(screenW(0.18f), screenH(0.05f))
                .setCenter(screenW(0.08f), screenH(0.97f)))
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
                .setSmartText(true)
                .setColor(Settings.CREAM_COLOR);

        loadoutEditor = new EUIButton(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(startingDeck.hb.x + scale(30), startingDeck.hb.y - scale(90), scale(150), scale(52)))
                .setTooltip(PGR.core.strings.csel_deckEditor, PGR.core.strings.csel_deckEditorInfo)
                .setLabel(EUIFontHelper.cardDescriptionFontNormal, 0.9f, PGR.core.strings.csel_deckEditor)
                .setColor(new Color(0.3f, 0.5f, 0.8f, 1))
                .setOnClick(this::openLoadoutEditor);

        Color panelColor = new Color(0.08f, 0.08f, 0.08f, 1);
        previewCardsInfo = new EUITextBox(panelTexture, new EUIHitbox(xPos, getY.invoke(2.5f), buttonWidth, screenH(0.15f)))
                .setLabel(EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, PGR.core.strings.sui_instructions1, PGR.core.strings.sui_instructions2))
                .setAlignment(0.85f, 0.1f, true)
                .setColors(panelColor, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 1f);
        typesAmount = new EUITextBox(panelTexture, new EUIHitbox(xPos, getY.invoke(3.9f), buttonWidth, screenH(0.09f)))
                .setColors(panelColor, Settings.GOLD_COLOR)
                .setAlignment(0.7f, 0.1f, true)
                .setFont(EUIFontHelper.cardTipTitleFont, 1);

        previewCards = EUIButton.createHexagonalButton(xPos, getY.invoke(6.3f), buttonWidth, buttonHeight)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, PGR.core.strings.sui_showCardPool)
                .setOnClick(() -> previewCardPool(null))
                .setColor(Color.LIGHT_GRAY);

        colorless = EUIButton.createHexagonalButton(xPos, getY.invoke(7.1f), buttonWidth, buttonHeight)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, PGR.core.strings.sui_showColorless)
                .setOnClick(this::previewColorless)
                .setColor(Color.LIGHT_GRAY);

        selectAllButton = EUIButton.createHexagonalButton(xPos, getY.invoke(7.9f), buttonWidth, buttonHeight)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, PGR.core.strings.sui_selectAll)
                .setTooltip(PGR.core.strings.sui_selectAll, PGR.core.strings.sui_selectAllDesc)
                .setOnClick(() -> this.selectAll(true))
                .setColor(Color.ROYAL);

        deselectAllButton = EUIButton.createHexagonalButton(xPos, getY.invoke(8.7f), buttonWidth, buttonHeight)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, PGR.core.strings.sui_deselectAll)
                .setTooltip(PGR.core.strings.sui_deselectAll, PGR.core.strings.sui_deselectDesc)
                .setOnClick(() -> this.selectAll(false))
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

    public void cancel() {
        SingleCardViewPopup.isViewingUpgrade = false;
        cardGrid.clear();
        close();
    }

    public void chooseSeries(AbstractCard card) {
        if (container.selectCard(EUIUtils.safeCast(card, PCLCard.class))) {
            updateStartingDeckText();
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        toggleViewUpgrades(false);

        if (onClose != null) {
            onClose.invoke();
        }
    }

    public void forceUpdateText() {
        container.calculateCardCounts();
        totalCardsCache = container.shownCards.size();
        totalColorlessCache = container.shownColorlessCards.size();
        totalCardsChanged(totalCardsCache, totalColorlessCache);
    }

    public CardGroup getCardPool(PCLLoadout loadout) {
        final CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        if (loadout != null) {
            for (PCLCardData data : loadout.cardDatas) {
                AbstractCard nc = data.makeCardFromLibrary(SingleCardViewPopup.isViewingUpgrade ? 1 : 0);
                if (nc instanceof PCLCard) {
                    ((PCLCard) nc).affinities.updateSortedList();
                }
                cards.group.add(nc);
            }
        }
        else {
            for (PCLLoadout cs : container.getAllLoadouts()) {
                for (PCLCardData data : cs.cardDatas) {
                    AbstractCard nc = data.makeCardFromLibrary(SingleCardViewPopup.isViewingUpgrade ? 1 : 0);
                    if (nc instanceof PCLCard) {
                        ((PCLCard) nc).affinities.updateSortedList();
                    }
                    cards.group.add(nc);
                }
            }
        }
        cards.sortAlphabetically(true);
        cards.sortByRarity(true);
        return cards;
    }

    public CardGroup getColorlessPool() {
        final CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        if (data != null) {
            for (AbstractCard c : CustomCardLibraryScreen.CardLists.get(AbstractCard.CardColor.COLORLESS).group) {
                if (PCLLoadoutsContainer.isRarityAllowed(c.rarity, c.type) &&
                        data.resources.containsColorless(c)) {
                    cards.addToBottom(c.makeCopy());
                }
            }
        }
        else {
            for (AbstractCard c : CustomCardLibraryScreen.CardLists.get(AbstractCard.CardColor.COLORLESS).group) {
                for (PCLResources<?, ?, ?, ?> resources : PGR.getRegisteredResources()) {
                    if (PCLLoadoutsContainer.isRarityAllowed(c.rarity, c.type) && !resources.filterColorless(c)) {
                        cards.addToBottom(c.makeCopy());
                    }
                }
            }
        }

        cards.sortAlphabetically(true);
        cards.sortByRarity(true);
        return cards;
    }

    protected void onCardClicked(AbstractCard card) {
        if (!isScreenDisabled) {
            chooseSeries(card);
        }
    }

    // Since core sets cannot be toggled, only show the view card option for them
    public void onCardRightClicked(AbstractCard card) {
        selectedCard = EUIUtils.safeCast(card, PCLCard.class);
        PCLLoadout c = container.find(selectedCard);
        if (!c.isLocked()) {
            contextMenu.setItems(card.type != PCLLoadout.SELECTABLE_TYPE ? EUIUtils.array(ContextOption.ViewCards) : ContextOption.values());
            contextMenu.positionToOpen();
        }
    }

    public void open(CharacterOption characterOption, AbstractPlayerData<?, ?> data, ActionT0 onClose) {
        super.open();

        this.onClose = onClose;
        this.characterOption = characterOption;
        this.data = data;

        container.createCards(data);
        cardGrid.addCards(container.getAllCards());
        updateStartingDeckText();

        EUI.countingPanel.open(container.shownCards, data.resources.cardColor, false);

        EUITourTooltip.queueFirstView(PGR.config.tourSeriesSelect,
                new EUITourTooltip(cardGrid.cards.group.get(1).hb, PGR.core.strings.csel_seriesEditor, PGR.core.strings.sui_instructions1)
                        .setPosition(Settings.WIDTH * 0.25f, Settings.HEIGHT * 0.75f),
                new EUITourTooltip(cardGrid.cards.group.get(1).hb, PGR.core.strings.csel_seriesEditor, PGR.core.strings.sui_instructions2)
                        .setPosition(Settings.WIDTH * 0.25f, Settings.HEIGHT * 0.75f),
                new EUITourTooltip(cardGrid.cards.group.get(0).hb, PGR.core.strings.csel_seriesEditor, PGR.core.strings.sui_coreInstructions)
                        .setPosition(Settings.WIDTH * 0.20f, Settings.HEIGHT * 0.75f),
                new EUITourTooltip(typesAmount.hb, PGR.core.strings.csel_seriesEditor, PGR.core.strings.sui_totalInstructions)
                        .setPosition(Settings.WIDTH * 0.6f, Settings.HEIGHT * 0.75f),
                loadoutEditor.makeTour(true)
        );
    }

    protected void openLoadoutEditor() {
        PCLLoadout current = container.find(container.currentSeriesCard);
        if (characterOption != null && current != null && data != null) {
            proceed();
            PGR.loadoutEditor.open(current, data, characterOption, this.onClose);
        }
    }

    public void previewCardPool(AbstractCard source) {
        if (container.shownCards.size() > 0) {
            PCLLoadout loadout = null;
            if (source != null) {
                source.unhover();
                loadout = container.find(EUIUtils.safeCast(source, PCLCard.class));
            }
            final CardGroup cards = getCardPool(loadout);
            previewCards(cards, loadout);
        }
    }

    // Core loadout cards cannot be toggled off
    public void previewCards(CardGroup cards, PCLLoadout loadout) {
        previewCardsEffect = new ViewInGameCardPoolEffect(cards, container.bannedCards, this::forceUpdateText)
                .setCanToggle(loadout != null && !loadout.isCore())
                .setStartingPosition(InputHelper.mX, InputHelper.mY);
        PCLEffects.Manual.add(previewCardsEffect);
    }

    public void previewColorless() {
        previewCardsEffect = new ViewInGameCardPoolEffect(getColorlessPool(), container.bannedColorless, this::forceUpdateText)
                .setStartingPosition(InputHelper.mX, InputHelper.mY);
        PCLEffects.Manual.add(previewCardsEffect);
    }

    public void proceed() {
        SingleCardViewPopup.isViewingUpgrade = false;
        cardGrid.clear();
        container.commitChanges(data);
        close();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        cardGrid.tryRender(sb);

        startingDeck.tryRender(sb);
        loadoutEditor.tryRender(sb);
        selectAllButton.tryRender(sb);
        deselectAllButton.tryRender(sb);
        previewCards.renderImpl(sb);
        colorless.renderImpl(sb);
        cancel.renderImpl(sb);
        confirm.renderImpl(sb);

        previewCardsInfo.renderImpl(sb);
        typesAmount.renderImpl(sb);

        if (previewCardsEffect != null) {
            previewCardsEffect.render(sb);
        }
        else {
            EUI.countingPanel.tryRender(sb);
        }

        contextMenu.tryRender(sb);
    }

    public void selectAll(boolean value) {
        for (PCLLoadout c : container.getAllLoadouts()) {
            container.toggleCards(c, value);
        }
    }

    public void togglePool(AbstractCard card, boolean value) {
        container.toggleCards(container.find(EUIUtils.safeCast(card, PCLCard.class)), value);
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
    }

    protected void totalCardsChanged(int totalCards, int totalColorless) {
        if (EUI.countingPanel.isActive) {
            EUI.countingPanel.open(container.shownCards, data.resources.cardColor, false);
        }

        typesAmount.setLabel(PGR.core.strings.sui_totalCards(
                totalCards >= MINIMUM_CARDS ? "g" : "r",
                totalCards,
                MINIMUM_CARDS,
                totalColorless >= MINIMUM_COLORLESS ? "g" : "r",
                totalColorless,
                MINIMUM_COLORLESS));

        confirm.setInteractable(container.isValid());
    }

    @Override
    public void updateImpl() {
        CardCrawlGame.mainMenuScreen.screenColor.a = MathHelper.popLerpSnap(CardCrawlGame.mainMenuScreen.screenColor.a, 0.8F);

        EUI.countingPanel.tryUpdate();

        if (previewCardsEffect != null) {
            previewCardsEffect.update();

            if (previewCardsEffect.isDone) {
                previewCardsEffect = null;
            }
            else {
                return;
            }
        }
        else {
            super.updateImpl();
        }

        if (totalCardsCache != container.shownCards.size() || totalColorlessCache != container.shownColorlessCards.size()) {
            totalCardsCache = container.shownCards.size();
            totalColorlessCache = container.shownColorlessCards.size();
            totalCardsChanged(totalCardsCache, totalColorlessCache);
        }

        startingDeck.tryUpdate();
        loadoutEditor.tryUpdate();

        if (!isScreenDisabled) {
            selectAllButton.tryUpdate();
            deselectAllButton.tryUpdate();
            previewCards.updateImpl();
            colorless.updateImpl();
            cancel.updateImpl();
            confirm.updateImpl();
            cardGrid.tryUpdate();
            previewCardsInfo.tryUpdate();
            typesAmount.tryUpdate();
        }

        contextMenu.tryUpdate();
    }

    protected void updateStartingDeckText() {
        startingDeck.setLabel(PGR.core.strings.csel_leftText + EUIUtils.SPLIT_LINE + PCLCoreStrings.colorString("y", (container.currentSeriesCard != null) ? container.currentSeriesCard.name : ""));
    }

    public enum ContextOption {
        Deselect(PGR.core.strings.sui_removeFromPool, (s, c) -> s.togglePool(c, false)),
        Select(PGR.core.strings.sui_addToPool, (s, c) -> s.togglePool(c, true)),
        ViewCards(PGR.core.strings.sui_viewPool, (screen, card) -> {
            if (screen.previewCardsEffect == null) {
                screen.previewCardPool(card);
            }
        });

        public final String name;
        public final ActionT2<PCLSeriesSelectScreen, AbstractCard> onSelect;

        ContextOption(String name, ActionT2<PCLSeriesSelectScreen, AbstractCard> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }

}
