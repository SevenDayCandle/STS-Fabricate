package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.screen.ViewInGamePoolEffect;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.resources.pcl.PCLCoreStrings;

import static pinacolada.ui.characterSelection.PCLLoadoutsContainer.MINIMUM_CARDS;

// Copied and modified from STS-AnimatorMod
public class PCLSeriesSelectScreen extends AbstractMenuScreen {
    public final PCLLoadoutsContainer container = new PCLLoadoutsContainer();
    public final EUICardGrid cardGrid;
    public final EUILabel startingDeck;
    public final EUIButton selectAllButton;
    public final EUIButton deselectAllButton;
    public final EUIButton previewCards;
    public final EUIButton cancel;
    public final EUIButton confirm;
    public final EUIButton loadoutEditor;
    public final EUITextBox typesAmount;
    public final EUITextBox previewCardsInfo;
    public final EUIContextMenu<ContextOption> contextMenu;
    protected PCLCard selectedCard;
    protected ActionT0 onClose;
    protected ViewInGamePoolEffect previewCardsEffect;
    protected CharacterOption characterOption;
    protected PCLAbstractPlayerData data;
    protected int totalCardsCache = 0;
    public boolean isScreenDisabled;

    public PCLSeriesSelectScreen() {
        final Texture panelTexture = EUIRM.images.panel.texture();
        final FuncT1<Float, Float> getY = (delta) -> screenH(0.95f) - screenH(0.07f * delta);
        final float buttonHeight = screenH(0.06f);
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

        loadoutEditor = new EUIButton(PCLCoreImages.Menu.swapCards.texture(), new EUIHitbox(0, 0, scale(64), scale(64)))
                .setPosition(startingDeck.hb.x + scale(80), startingDeck.hb.y - scale(48)).setText("")
                .setTooltip(PGR.core.strings.csel_deckEditor, PGR.core.strings.csel_deckEditorInfo)
                .setOnClick(this::openLoadoutEditor);

        previewCardsInfo = new EUITextBox(panelTexture, new EUIHitbox(xPos, getY.invoke(2.5f), buttonWidth, buttonHeight * 3f))
                .setLabel(PGR.core.strings.sui_instructions1)
                .setAlignment(0.9f, 0.1f, true)
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 1f);

        typesAmount = new EUITextBox(panelTexture, new EUIHitbox(xPos, getY.invoke(3.5f), buttonWidth, buttonHeight))
                .setColors(Color.DARK_GRAY, Settings.GOLD_COLOR)
                .setAlignment(0.61f, 0.1f, true)
                .setFont(EUIFontHelper.cardTipTitleFont, 1);

        previewCards = createHexagonalButton(xPos, getY.invoke(7f), buttonWidth, buttonHeight)
                .setText(PGR.core.strings.sui_showCardPool)
                .setOnClick(() -> previewCardPool(null))
                .setColor(Color.LIGHT_GRAY);

        selectAllButton = createHexagonalButton(xPos, getY.invoke(8f), buttonWidth, buttonHeight)
                .setText(PGR.core.strings.sui_selectAll)
                .setOnClick(() -> this.selectAll(true))
                .setColor(Color.ROYAL);

        deselectAllButton = createHexagonalButton(xPos, getY.invoke(9f), buttonWidth, buttonHeight)
                .setText(PGR.core.strings.sui_deselectAll)
                .setOnClick(() -> this.selectAll(false))
                .setColor(Color.FIREBRICK);

        cancel = createHexagonalButton(xPos, getY.invoke(12f), buttonWidth, buttonHeight * 1.2f)
                .setText(PGR.core.strings.sui_cancel)
                .setOnClick(this::cancel)
                .setColor(Color.FIREBRICK);

        confirm = createHexagonalButton(xPos, getY.invoke(13f), buttonWidth, buttonHeight * 1.2f)
                .setText(PGR.core.strings.sui_save)
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
        AbstractDungeon.closeCurrentScreen();
    }

    public void chooseSeries(AbstractCard card) {
        if (container.selectCard(EUIUtils.safeCast(card, PCLCard.class))) {
            updateStartingDeckText();
        }
    }

    public void forceUpdateText() {
        container.calculateCardCounts();
        totalCardsCache = container.totalCardsInPool;
        totalCardsChanged(totalCardsCache);
    }

    public CardGroup getCardPool(PCLLoadout loadout) {
        final CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        if (loadout != null) {
            for (PCLCardData data : loadout.cardDatas) {
                AbstractCard nc = data.makeCardFromLibrary(SingleCardViewPopup.isViewingUpgrade ? 1 : 0);
                cards.group.add(nc);
            }
        }
        else {
            for (PCLLoadout cs : container.getAllLoadouts()) {
                for (PCLCardData data : cs.cardDatas) {
                    AbstractCard nc = data.makeCardFromLibrary(SingleCardViewPopup.isViewingUpgrade ? 1 : 0);
                    cards.group.add(nc);
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

        contextMenu.setPosition(InputHelper.mX, InputHelper.mY);
        contextMenu.setItems(card.type == PCLLoadout.UNSELECTABLE_TYPE ? EUIUtils.array(ContextOption.ViewCards) : ContextOption.values());
        contextMenu.openOrCloseMenu();
    }

    public void open(CharacterOption characterOption, PCLAbstractPlayerData data, ActionT0 onClose) {
        super.open();
        this.onClose = onClose;
        this.characterOption = characterOption;
        this.data = data;

        container.createCards(data);
        cardGrid.addCards(container.getAllCards());
        updateStartingDeckText();

        EUI.countingPanel.open(container.allCards);
    }

    protected void openLoadoutEditor() {
        PCLLoadout current = container.find(container.currentSeriesCard);
        if (characterOption != null && current != null && data != null) {
            proceed();
            PGR.loadoutEditor.open(current, data, characterOption, this.onClose);
        }
    }

    public void previewCardPool(AbstractCard source) {
        if (container.totalCardsInPool > 0) {
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
        previewCardsEffect = new ViewInGamePoolEffect(cards, container.bannedCards, this::forceUpdateText)
                .setCanToggle(loadout != null && !loadout.isCore())
                .setStartingPosition(InputHelper.mX, InputHelper.mY);
        PCLEffects.Manual.add(previewCardsEffect);
    }

    public void proceed() {
        SingleCardViewPopup.isViewingUpgrade = false;
        cardGrid.clear();
        container.commitChanges(data);
        if (onClose != null) {
            onClose.invoke();
        }
        AbstractDungeon.closeCurrentScreen();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        PGR.blackScreen.renderImpl(sb);

        cardGrid.tryRender(sb);

        startingDeck.tryRender(sb);
        loadoutEditor.tryRender(sb);
        selectAllButton.tryRender(sb);
        deselectAllButton.tryRender(sb);
        previewCards.renderImpl(sb);
        cancel.renderImpl(sb);
        confirm.renderImpl(sb);

        typesAmount.renderImpl(sb);
        previewCardsInfo.renderImpl(sb);

        if (previewCardsEffect != null) {
            previewCardsEffect.render(sb);
        }
        else {
            EUI.countingPanel.tryRender(sb);
        }

        contextMenu.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        PGR.blackScreen.updateImpl();
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

        if (totalCardsCache != container.totalCardsInPool) {
            totalCardsCache = container.totalCardsInPool;
            totalCardsChanged(totalCardsCache);
        }

        startingDeck.tryUpdate();
        loadoutEditor.tryUpdate();

        if (!isScreenDisabled) {
            selectAllButton.tryUpdate();
            deselectAllButton.tryUpdate();
            previewCards.updateImpl();
            cancel.updateImpl();
            confirm.updateImpl();
            cardGrid.tryUpdate();
            typesAmount.tryUpdate();
        }

        contextMenu.tryUpdate();
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

    protected void totalCardsChanged(int totalCards) {
        if (EUI.countingPanel.isActive) {
            EUI.countingPanel.open(container.allCards);
        }

        typesAmount.setLabel(PCLCoreStrings.colorString(totalCards >= MINIMUM_CARDS ? "g" : "r", PGR.core.strings.sui_totalCards(totalCards, MINIMUM_CARDS)));

        confirm.setInteractable(container.isValid());
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
