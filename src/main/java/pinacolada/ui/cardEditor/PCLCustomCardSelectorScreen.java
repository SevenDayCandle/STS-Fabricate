package pinacolada.ui.cardEditor;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PCLCustomCardSelectorScreen extends AbstractScreen
{

    protected static final float ITEM_HEIGHT = AbstractCard.IMG_HEIGHT * 0.15f;
    private static final float DRAW_START_X = (Settings.WIDTH - (5f * AbstractCard.IMG_WIDTH * 0.75f) - (4f * Settings.CARD_VIEW_PAD_X) + AbstractCard.IMG_WIDTH * 0.75f);
    private static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    private static final float PAD_Y = AbstractCard.IMG_HEIGHT * 0.75f + Settings.CARD_VIEW_PAD_Y;
    private static final float SCROLL_BAR_THRESHOLD = 500f * Settings.scale;
    public static AbstractCard.CardColor currentColor = AbstractCard.CardColor.COLORLESS;
    public static AbstractPlayer.PlayerClass currentClass;
    protected final EUIStaticCardGrid grid;
    protected final EUIToggle toggle;
    protected ActionT0 onClose;
    protected EUIButton addButton;
    protected EUIButton cancelButton;
    protected EUIButton openButton;
    protected EUIButton reloadButton;
    protected EUIButtonList colorButtons;
    protected EUIContextMenu<ContextOption> contextMenu;
    protected EUIImage backgroundImage;
    protected EUITextBox info;
    protected HashMap<AbstractCard, PCLCustomCardSlot> currentSlots = new HashMap<>();
    protected PCLEffectWithCallback currentEffect;
    private AbstractCard clickedCard;

    public PCLCustomCardSelectorScreen()
    {
        final float buttonHeight = screenH(0.06f);
        final float labelHeight = screenH(0.04f);
        final float buttonWidth = screenW(0.18f);
        final float labelWidth = screenW(0.20f);

        backgroundImage = new EUIImage(EUIRM.images.fullSquare.texture(), new EUIHitbox(screenW(1), screenH(1)))
                .setPosition(screenW(0.5f), screenH(0.5f))
                .setColor(0, 0, 0, 0.9f);

        this.grid = (EUIStaticCardGrid) new EUIStaticCardGrid(1f)
                .setEnlargeOnHover(false)
                .setOnCardClick(this::onCardClicked)
                .setOnCardRightClick(this::onCardRightClicked);
        toggle = new EUIToggle(new EUIHitbox(0, 0, AbstractCard.IMG_WIDTH * 0.2f, ITEM_HEIGHT))
                .setBackground(EUIRM.images.panel.texture(), com.badlogic.gdx.graphics.Color.DARK_GRAY)
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.65f)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f);
        cancelButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(buttonWidth * 0.53f, buttonHeight)
                .setColor(Color.FIREBRICK)
                .setText(GridCardSelectScreen.TEXT[1])
                .setOnClick(AbstractDungeon::closeCurrentScreen);

        addButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.FOREST)
                .setText(PGR.core.strings.cardEditor.newCard)
                .setOnClick(this::add);

        openButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(addButton.hb.cX, addButton.hb.y + addButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cardEditor.openFolder)
                .setOnClick(PCLCustomCardSelectorScreen::openFolder);

        reloadButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(openButton.hb.cX, openButton.hb.y + openButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cardEditor.reloadCards)
                .setTooltip(PGR.core.strings.cardEditor.reloadCards, PGR.core.strings.cardEditorTutorial.selectorReload)
                .setOnClick(PCLCustomCardSlot::initialize);

        contextMenu = (EUIContextMenu<ContextOption>) new EUIContextMenu<ContextOption>(new EUIHitbox(0, 0, 0, 0), o -> o.name)
                .setItems(ContextOption.values())
                .setOnChange(options -> {
                    for (ContextOption o : options)
                    {
                        o.onSelect.invoke(this, clickedCard, currentSlots.get(clickedCard));
                    }
                })
                .setCanAutosizeButton(true);
        info = new EUITextBox(EUIRM.images.panel.texture(), new EUIHitbox(screenW(0.25f), screenH(0.035f), screenW(0.5f), buttonHeight * 2.5f))
                .setLabel(EUIUtils.joinStrings(EUIUtils.DOUBLE_SPLIT_LINE, PGR.core.strings.cardEditorTutorial.selector1, PGR.core.strings.cardEditorTutorial.selector2))
                .setAlignment(0.75f, 0.1f, true)
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 0.9f);

        colorButtons = new EUIButtonList(14, screenW(0.09f), screenH(0.95f), EUIButtonList.BUTTON_W, scale(47));

        for (AbstractCard.CardColor color : getAllColors())
        {
            makeColorButton(color);
        }
    }

    public static void openFolder()
    {
        try
        {
            Desktop.getDesktop().open(Gdx.files.local(PCLCustomCardSlot.FOLDER).file());
        }
        catch (Exception e)
        {
            EUIUtils.logError(null, "Failed to open card folder.");
        }
    }

    public void add()
    {
        if (currentEffect == null)
        {
            PCLCustomCardSlot slot = new PCLCustomCardSlot(currentColor);
            currentEffect = new PCLCustomCardEditCardScreen(slot)
                    .setOnSave(() -> {
                        AbstractCard newCard = slot.getBuilder(0).build();
                        currentSlots.put(newCard, slot);
                        PCLCustomCardSlot.getCards(currentColor).add(slot);
                        grid.addCard(newCard);
                        slot.commitBuilder();
                    });
        }
    }

    public void duplicate(AbstractCard card, PCLCustomCardSlot cardSlot)
    {
        if (currentEffect == null && cardSlot != null)
        {
            PCLCustomCardSlot slot = new PCLCustomCardSlot(cardSlot);
            currentEffect = new PCLCustomCardEditCardScreen(slot)
                    .setOnSave(() -> {
                        AbstractCard newCard = slot.getBuilder(0).build();
                        currentSlots.put(newCard, slot);
                        PCLCustomCardSlot.getCards(currentColor).add(slot);
                        grid.addCard(newCard);
                        slot.commitBuilder();
                    });
        }
    }

    public void duplicateToColor(AbstractCard card, PCLCustomCardSlot cardSlot)
    {
        if (currentEffect == null && cardSlot != null)
        {
            currentEffect = new PCLCustomCardCopyConfirmationEffect(getAllColors())
                    .addCallback((co) -> {
                        if (co != null)
                        {
                            PCLCustomCardSlot slot = new PCLCustomCardSlot(cardSlot, co);
                            open(null, co, this.onClose);
                            currentEffect = new PCLCustomCardEditCardScreen(slot)
                                    .setOnSave(() -> {
                                        AbstractCard newCard = slot.getBuilder(0).build();
                                        currentSlots.put(newCard, slot);
                                        PCLCustomCardSlot.getCards(co).add(slot);
                                        grid.addCard(newCard);
                                        slot.commitBuilder();
                                    });
                        }
                    });
        }
    }

    public void edit(AbstractCard card, PCLCustomCardSlot cardSlot)
    {
        if (currentEffect == null && cardSlot != null)
        {
            currentEffect = new PCLCustomCardEditCardScreen(cardSlot)
                    .setOnSave(() -> {
                        AbstractCard newCard = cardSlot.getBuilder(0).build();
                        grid.removeCard(card);
                        currentSlots.remove(card);
                        currentSlots.put(newCard, cardSlot);
                        grid.addCard(newCard);
                        cardSlot.commitBuilder();
                    });
        }
    }

    private ArrayList<AbstractCard.CardColor> getAllColors()
    {
        ArrayList<AbstractCard.CardColor> list = new ArrayList<>();
        list.add(AbstractCard.CardColor.COLORLESS);
        list.add(AbstractCard.CardColor.RED);
        list.add(AbstractCard.CardColor.GREEN);
        list.add(AbstractCard.CardColor.BLUE);
        list.add(AbstractCard.CardColor.PURPLE);
        list.addAll(BaseMod.getCardColors().stream().sorted(Comparator.comparing(EUIGameUtils::getColorName)).collect(Collectors.toList()));
        return list;
    }

    private void makeColorButton(AbstractCard.CardColor co) {
        colorButtons.addButton(button -> open(null, co, this.onClose), EUIGameUtils.getColorName(co))
                .setColor(EUIGameUtils.getColorColor(co));
    }

    private void onCardClicked(AbstractCard card)
    {
        PCLCustomCardSlot slot = currentSlots.get(card);
        if (slot != null)
        {
            edit(card, slot);
        }
    }

    private void onCardRightClicked(AbstractCard card)
    {
        clickedCard = card;
        contextMenu.setPosition(InputHelper.mX, InputHelper.mY);
        contextMenu.openOrCloseMenu();
    }

    public void open(AbstractPlayer.PlayerClass playerClass, AbstractCard.CardColor cardColor, ActionT0 onClose)
    {
        super.open();

        currentClass = playerClass;
        currentColor = EUI.actingColor = cardColor;
        currentSlots.clear();
        grid.clear();
        for (PCLCustomCardSlot slot : PCLCustomCardSlot.getCards(currentColor))
        {
            AbstractCard card = slot.getBuilder(0).build();
            currentSlots.put(card, slot);
            grid.addCard(card);
        }
        EUI.customHeader.setGroup(grid.cards);
        EUI.cardFilters.initialize(__ -> {
            grid.moveToTop();
            grid.forceUpdateCardPositions();
        }, EUI.customHeader.originalGroup, currentColor, false);
    }

    public void remove(AbstractCard card, PCLCustomCardSlot cardSlot)
    {
        if (currentEffect == null && cardSlot != null)
        {
            currentEffect = new PCLCustomCardDeletionConfirmationEffect(cardSlot)
                    .addCallback((v) -> {
                        if (v != null)
                        {
                            grid.removeCard(card);
                            currentSlots.remove(card);
                            v.wipeBuilder();
                        }
                    });
        }
    }

    @Override
    public void updateImpl()
    {
        backgroundImage.updateImpl();
        if (currentEffect != null)
        {
            currentEffect.update();

            if (currentEffect.isDone)
            {
                currentEffect = null;
            }
        }
        else
        {
            boolean shouldDoStandardUpdate = !EUI.cardFilters.tryUpdate() && !CardCrawlGame.isPopupOpen;
            if (shouldDoStandardUpdate)
            {
                EUI.openCardFiltersButton.tryUpdate();
                info.tryUpdate();
                colorButtons.tryUpdate();
                grid.tryUpdate();
                cancelButton.tryUpdate();
                addButton.tryUpdate();
                openButton.tryUpdate();
                reloadButton.tryUpdate();
                contextMenu.tryUpdate();
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        backgroundImage.renderImpl(sb);
        if (currentEffect != null)
        {
            currentEffect.render(sb);
        }
        else
        {
            info.tryRender(sb);
            grid.tryRender(sb);
            cancelButton.tryRender(sb);
            addButton.tryRender(sb);
            openButton.tryRender(sb);
            reloadButton.tryRender(sb);
            contextMenu.tryRender(sb);
            colorButtons.tryRender(sb);
            if (!EUI.cardFilters.isActive) {
                EUI.openCardFiltersButton.tryRender(sb);
            }
        }
    }

    public enum ContextOption
    {
        Duplicate(PGR.core.strings.cardEditor.duplicate, PCLCustomCardSelectorScreen::duplicate),
        DuplicateToColor(PGR.core.strings.cardEditor.duplicateToColor, PCLCustomCardSelectorScreen::duplicateToColor),
        Delete(PGR.core.strings.cardEditor.delete, PCLCustomCardSelectorScreen::remove);

        public final String name;
        public final ActionT3<PCLCustomCardSelectorScreen, AbstractCard, PCLCustomCardSlot> onSelect;

        ContextOption(String name, ActionT3<PCLCustomCardSelectorScreen, AbstractCard, PCLCustomCardSlot> onSelect)
        {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}
