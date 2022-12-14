package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.CardAffinityComparator;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ShowCardPileEffect;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLRuntimeLoadout;
import pinacolada.utilities.GameUtilities;

import java.util.Collection;

import static pinacolada.ui.characterSelection.PCLLoadoutsContainer.MINIMUM_CARDS;

// TODO Card amount selector
public class PCLSeriesSelectScreen extends AbstractScreen
{
    public final PCLLoadoutsContainer container = new PCLLoadoutsContainer();
    public final EUIImage backgroundImage;
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
    public boolean isScreenDisabled;
    protected AbstractCard selectedCard;
    protected ActionT0 onClose;
    protected ShowCardPileEffect previewCardsEffect;
    protected CharacterOption characterOption;
    protected PCLAbstractPlayerData data;
    protected int totalCardsCache = 0;

    public PCLSeriesSelectScreen()
    {
        final Texture panelTexture = EUIRM.images.panel.texture();
        final FuncT1<Float, Float> getY = (delta) -> screenH(0.95f) - screenH(0.07f * delta);
        final float buttonHeight = screenH(0.06f);
        final float buttonWidth = screenW(0.18f);
        final float xPos = screenW(0.82f);

        backgroundImage = new EUIImage(EUIRM.images.fullSquare.texture(), new EUIHitbox(screenW(1), screenH(1)))
                .setPosition(screenW(0.5f), screenH(0.5f))
                .setColor(0, 0, 0, 0.85f);

        cardGrid = new EUICardGrid(0.41f, false)
                .setOnCardClick(this::onCardClicked)
                .setOnCardRightClick(this::onCardRightClicked)
                .showScrollbar(false);

        startingDeck = new EUILabel(null, new EUIHitbox(screenW(0.18f), screenH(0.05f))
                .setCenter(screenW(0.08f), screenH(0.97f)))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setSmartText(true)
                .setColor(Settings.CREAM_COLOR);

        loadoutEditor = new EUIButton(PGR.core.images.swapCards.texture(), new EUIHitbox(0, 0, scale(64), scale(64)))
                .setPosition(startingDeck.hb.x + scale(80), startingDeck.hb.y - scale(48)).setText("")
                .setTooltip(PGR.core.strings.charSelect.deckEditor, PGR.core.strings.charSelect.deckEditorInfo)
                .setOnClick(this::openLoadoutEditor);

        previewCardsInfo = new EUITextBox(panelTexture, new EUIHitbox(xPos, getY.invoke(2.5f), buttonWidth, buttonHeight * 3.5f))
                .setLabel(PGR.core.strings.seriesSelection.instructions1)
                .setAlignment(0.9f, 0.1f, true)
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(FontHelper.tipBodyFont, 1f);

        typesAmount = new EUITextBox(panelTexture, new EUIHitbox(xPos, getY.invoke(5.3f), buttonWidth, buttonHeight * 2.4f))
                .setColors(Color.DARK_GRAY, Settings.GOLD_COLOR)
                .setAlignment(0.9f, 0.1f, true)
                .setFont(FontHelper.tipHeaderFont, 1);

        previewCards = createHexagonalButton(xPos, getY.invoke(7f), buttonWidth, buttonHeight)
                .setText(PGR.core.strings.seriesSelectionButtons.showCardPool)
                .setOnClick(() -> previewCardPool(null))
                .setColor(Color.LIGHT_GRAY);

        selectAllButton = createHexagonalButton(xPos, getY.invoke(8f), buttonWidth, buttonHeight)
                .setText(PGR.core.strings.seriesSelectionButtons.selectAll)
                .setOnClick(() -> this.selectAll(true))
                .setColor(Color.ROYAL);

        deselectAllButton = createHexagonalButton(xPos, getY.invoke(9f), buttonWidth, buttonHeight)
                .setText(PGR.core.strings.seriesSelectionButtons.deselectAll)
                .setOnClick(() -> this.selectAll(false))
                .setColor(Color.FIREBRICK);

        cancel = createHexagonalButton(xPos, getY.invoke(12f), buttonWidth, buttonHeight * 1.2f)
                .setText(PGR.core.strings.seriesSelectionButtons.cancel)
                .setOnClick(this::cancel)
                .setColor(Color.FIREBRICK);

        confirm = createHexagonalButton(xPos, getY.invoke(13f), buttonWidth, buttonHeight * 1.2f)
                .setText(PGR.core.strings.seriesSelectionButtons.save)
                .setOnClick(this::proceed)
                .setColor(Color.FOREST);

        contextMenu = (EUIContextMenu<ContextOption>) new EUIContextMenu<ContextOption>(new EUIHitbox(0, 0, 0, 0), o -> o.name)
                .setOnOpenOrClose(isOpen -> isScreenDisabled = isOpen)
                .setOnChange(options -> {
                    for (ContextOption o : options)
                    {
                        o.onSelect.invoke(this, selectedCard);
                    }
                })
                .setCanAutosizeButton(true)
                .setItems(ContextOption.values());
    }

    public void togglePool(AbstractCard card, boolean value)
    {
        container.toggleCards(container.find(card), value);
    }

    public void cancel()
    {
        SingleCardViewPopup.isViewingUpgrade = false;
        cardGrid.clear();
        AbstractDungeon.closeCurrentScreen();
    }

    public void chooseSeries(AbstractCard card)
    {
        if (container.selectCard(card))
        {
            updateStartingDeckText();
        }
    }

    public CardGroup getCardPool(PCLRuntimeLoadout loadout)
    {
        final CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        if (loadout != null)
        {
            final Collection<AbstractCard> cardsSource = loadout.getCardPoolInPlay().values();
            for (AbstractCard c : cardsSource)
            {
                AbstractCard nc = c.makeStatEquivalentCopy();
                if (SingleCardViewPopup.isViewingUpgrade)
                {
                    nc.upgrade();
                }
                cards.group.add(nc);
            }
        }
        else
        {
            for (PCLRuntimeLoadout cs : container.getAllLoadouts())
            {
                final Collection<AbstractCard> cardsSource = cs.getCardPoolInPlay().values();
                for (AbstractCard c : cardsSource)
                {
                    AbstractCard nc = c.makeStatEquivalentCopy();
                    if (SingleCardViewPopup.isViewingUpgrade)
                    {
                        nc.upgrade();
                    }
                    cards.group.add(nc);
                }
            }
        }
        cards.sortAlphabetically(true);
        cards.sortByRarity(true);
        return cards;
    }

    protected void onCardClicked(AbstractCard card)
    {
        if (!isScreenDisabled)
        {
            chooseSeries(card);
        }
    }

    public void onCardRightClicked(AbstractCard card)
    {
        selectedCard = card;
        PCLRuntimeLoadout c = container.find(card);

        contextMenu.setPosition(InputHelper.mX, InputHelper.mY);
        contextMenu.openOrCloseMenu();
    }

    public void open(CharacterOption characterOption, PCLAbstractPlayerData data, ActionT0 onClose)
    {
        super.open();
        this.onClose = onClose;
        this.characterOption = characterOption;
        this.data = data;

        container.createCards(data);
        cardGrid.addCards(container.getAllCards());
        updateStartingDeckText();

        PGR.core.cardAffinities.setActive(true);
        PGR.core.cardAffinities.open(container.getAllCardsInPool(),
                false,
                (c) -> {
                    CardGroup group = GameUtilities.createCardGroup(c.affinityGroup.getCards());
                    if (group.size() > 0 && previewCardsEffect == null)
                    {
                        group.sortByRarity(true);
                        group.sortAlphabetically(true);
                        group.group.sort(new CardAffinityComparator(c.type));
                        previewCards(group, null);
                    }
                },
                true);
    }

    protected void openLoadoutEditor()
    {
        PCLRuntimeLoadout current = container.find(container.currentSeriesCard);
        if (characterOption != null && current != null && data != null)
        {
            proceed();
            PGR.core.loadoutEditor.open(current.loadout, data, characterOption, this.onClose);
        }
    }

    public void previewCardPool(AbstractCard source)
    {
        if (container.totalCardsInPool > 0)
        {
            PCLRuntimeLoadout loadout = null;
            if (source != null)
            {
                source.unhover();
                loadout = container.find(source);
            }
            final CardGroup cards = getCardPool(loadout);
            previewCards(cards, loadout);
        }
    }

    public void previewCards(CardGroup cards, PCLRuntimeLoadout loadout)
    {
        previewCardsEffect = new ShowCardPileEffect(this, cards)
                .setStartingPosition(InputHelper.mX, InputHelper.mY);
        PCLEffects.Manual.add(previewCardsEffect);
    }

    public void proceed()
    {
        SingleCardViewPopup.isViewingUpgrade = false;
        cardGrid.clear();
        container.commitChanges(data);
        if (onClose != null)
        {
            onClose.invoke();
        }
        AbstractDungeon.closeCurrentScreen();
    }

    public void selectAll(boolean value)
    {
        for (PCLRuntimeLoadout c : container.getAllLoadouts())
        {
            container.toggleCards(c, value);
        }
    }

    private void toggleViewUpgrades(boolean value)
    {
        SingleCardViewPopup.isViewingUpgrade = value;
    }

    protected void totalCardsChanged(int totalCards)
    {
        if (PGR.core.cardAffinities.isActive)
        {
            PGR.core.cardAffinities.open(container.getAllCardsInPool(), false, c ->
            {
                CardGroup group = GameUtilities.createCardGroup(c.affinityGroup.getCards());
                if (group.size() > 0 && previewCardsEffect == null)
                {
                    group.sortByRarity(true);
                    group.sortAlphabetically(true);
                    group.group.sort(new CardAffinityComparator(c.type));

                    previewCards(group, null);
                }
            }, true);
        }

        typesAmount.setLabel(EUIUtils.joinStrings(" | ",
                stringForRarity(AbstractCard.CardRarity.COMMON),
                stringForRarity(AbstractCard.CardRarity.UNCOMMON),
                stringForRarity(AbstractCard.CardRarity.RARE),
                "{#" + (totalCards >= MINIMUM_CARDS ? 'g' : 'r') + ":" + PGR.core.strings.seriesSelection.totalCards(totalCards, MINIMUM_CARDS) + "}"
        ));

        confirm.setInteractable(container.isValid());
    }

    public void forceUpdateText()
    {
        container.calculateCardCounts();
        totalCardsCache = container.totalCardsInPool;
        totalCardsChanged(totalCardsCache);
    }

    @Override
    public void updateImpl()
    {
        backgroundImage.updateImpl();
        PGR.core.cardAffinities.tryUpdate(true);

        if (previewCardsEffect != null)
        {
            previewCardsEffect.update();

            if (previewCardsEffect.isDone)
            {
                previewCardsEffect = null;
            }
            else
            {
                return;
            }
        }

        if (totalCardsCache != container.totalCardsInPool)
        {
            totalCardsCache = container.totalCardsInPool;
            totalCardsChanged(totalCardsCache);
        }

        startingDeck.tryUpdate();
        loadoutEditor.tryUpdate();

        if (!isScreenDisabled)
        {
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

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        backgroundImage.renderImpl(sb);

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

        if (previewCardsEffect != null)
        {
            previewCardsEffect.render(sb);
        }
        else
        {
            PGR.core.cardAffinities.tryRender(sb);
        }

        contextMenu.tryRender(sb);
    }

    protected String stringForRarity(AbstractCard.CardRarity rarity)
    {
        int typeAmount = container.rarityCount.getOrDefault(rarity, 0);
        return EUIGameUtils.textForRarity(rarity)
                + ": {#" + (typeAmount >= container.getMinimum(rarity) ? 'g' : 'r') + ":"
                + typeAmount + "/" + container.getMinimum(rarity) + "}";
    }

    protected void updateStartingDeckText()
    {
        startingDeck.setLabel(PGR.core.strings.charSelect.leftText + " | {#y:" + ((container.currentSeriesCard != null) ? container.currentSeriesCard.name : "") + "}");
    }

    public enum ContextOption
    {
        Deselect(PGR.core.strings.seriesSelection.removeFromPool, (s, c) -> s.togglePool(c, false)),
        Select(PGR.core.strings.seriesSelection.addToPool, (s, c) -> s.togglePool(c, true)),
        ViewCards(PGR.core.strings.seriesSelection.viewPool, (screen, card) -> {
            if (screen.previewCardsEffect == null)
            {
                screen.previewCardPool(card);
            }
        });

        public final String name;
        public final ActionT2<PCLSeriesSelectScreen, AbstractCard> onSelect;

        ContextOption(String name, ActionT2<PCLSeriesSelectScreen, AbstractCard> onSelect)
        {
            this.name = name;
            this.onSelect = onSelect;
        }
    }

}
