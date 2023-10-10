package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUICardGrid;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.ui.characterSelection.PCLCardSlotEditor;

// Copied and modified from STS-AnimatorMod
public class PCLCardSlotSelectionEffect extends PCLEffectWithCallback<PCLCardSlotSelectionEffect> {
    private static final EUITextBox cardValue_text = new
            EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(AbstractCard.IMG_WIDTH * 0.6f, AbstractCard.IMG_HEIGHT * 0.15f))
            .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
            .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
            .setAlignment(0.5f, 0.5f)
            .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

    private final boolean draggingScreen = false;
    public final PCLCardSlotEditor slot;
    private AbstractCard selectedCard;
    private EUICardGrid grid;

    public PCLCardSlotSelectionEffect(PCLCardSlotEditor slot) {
        super(0.7f, true);

        this.slot = slot;

        if (slot.getAvailableCards().isEmpty()) {
            complete();
            return;
        }

        this.grid = (EUICardGrid) new EUICardGrid()
                .addPadY(AbstractCard.IMG_HEIGHT * 0.15f)
                .setEnlargeOnHover(false)
                .setOnClick(this::onCardClicked)
                .setOnRender(this::onCardRender);

        for (String item : slot.getAvailableCards()) {
            AbstractCard card = CardLibrary.getCard(item);
            if (card != null) {
                card.current_x = InputHelper.mX;
                card.current_y = InputHelper.mY;
                grid.add(card);
                if (card instanceof PCLCard) {
                    ((PCLCard) card).affinities.updateSortedList();
                }
            }
        }

        if (slot.slot != null) {
            this.selectedCard = EUIUtils.find(grid.group, s -> s.cardID.equals(slot.slot.selected));
        }

        EUI.cardFilters.initializeForSort(grid.group, __ -> {
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, EUI.actingColor);
        grid.group.sort(this::defaultSort);
    }

    private int defaultSort(AbstractCard a, AbstractCard b) {
        int aVal = getCardValue(a);
        int bVal = getCardValue(b);
        if (aVal < 0) {
            aVal = aVal * -1000;
        }
        if (bVal < 0) {
            bVal = bVal * -1000;
        }
        return aVal - bVal;
    }

    private int getCardValue(AbstractCard card) {
        return (card instanceof PCLCard && !(card instanceof PCLDynamicCard)) ? ((PCLCard) card).cardData.loadoutValue : card != null ? PCLCardData.getValueForRarity(card.rarity) : 0;
    }

    public AbstractCard getSelectedCard() {
        return selectedCard;
    }

    private void onCardClicked(AbstractCard card) {
        selectedCard = card;
        CardCrawlGame.sound.play("CARD_SELECT");
        complete(this);
    }

    private void onCardRender(SpriteBatch sb, AbstractCard card) {
        int estimateValue = getCardValue(card);
        cardValue_text
                .setLabel(estimateValue)
                .setFontColor(estimateValue < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR)
                .setPosition(card.hb.cX, card.hb.cY - (card.hb.height * 0.65f))
                .renderImpl(sb);
    }

    @Override
    public void render(SpriteBatch sb) {
        grid.tryRender(sb);
        EUI.sortHeader.render(sb);
        if (!EUI.cardFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
        }
    }

    @Override
    protected void updateInternal(float deltaTime) {
        boolean shouldDoStandardUpdate = !EUI.cardFilters.tryUpdate();
        if (shouldDoStandardUpdate) {
            grid.tryUpdate();
            EUI.sortHeader.update();
            EUI.openFiltersButton.update();

            if (InputHelper.justClickedLeft && !grid.isHovered() && !EUI.openFiltersButton.hb.hovered && !EUI.sortHeader.isHovered()) {
                complete();
            }
        }
    }
}