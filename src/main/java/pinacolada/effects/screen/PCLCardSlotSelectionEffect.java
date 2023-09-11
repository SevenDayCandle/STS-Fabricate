package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRM;
import extendedui.ui.controls.EUICardGrid;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.loadout.LoadoutCardSlot;
import pinacolada.ui.characterSelection.PCLCardSlotEditor;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLCardSlotSelectionEffect extends PCLEffectWithCallback<PCLCardSlotSelectionEffect> {
    private static final EUITextBox cardValue_text = new
            EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(AbstractCard.IMG_WIDTH * 0.6f, AbstractCard.IMG_HEIGHT * 0.15f))
            .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
            .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
            .setAlignment(0.5f, 0.5f)
            .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

    private final boolean draggingScreen = false;
    private AbstractCard selectedCard;
    private EUICardGrid grid;
    public final PCLCardSlotEditor slot;

    public PCLCardSlotSelectionEffect(PCLCardSlotEditor slot) {
        super(0.7f, true);

        this.slot = slot;

        if (slot.getAvailableCards().isEmpty()) {
            complete();
            return;
        }

        this.grid = new EUICardGrid()
                .addPadY(AbstractCard.IMG_HEIGHT * 0.15f)
                .setEnlargeOnHover(false)
                .setOnCardClick(this::onCardClicked)
                .setOnCardRender(this::onCardRender);

        for (String item : slot.getAvailableCards()) {
            AbstractCard card = CardLibrary.getCard(item);
            if (item != null) {
                card.current_x = InputHelper.mX;
                card.current_y = InputHelper.mY;
                grid.addCard(card);
            }
        }

        if (slot.slot != null) {
            this.selectedCard = grid.cards.findCardById(slot.slot.selected);
        }
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
        int estimateValue = card instanceof PCLCard ? ((PCLCard) card).cardData.loadoutValue : PCLCardData.getValueForRarity(card.rarity);
        cardValue_text
                .setLabel(estimateValue)
                .setFontColor(estimateValue < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR)
                .setPosition(card.hb.cX, card.hb.cY - (card.hb.height * 0.65f))
                .renderImpl(sb);
    }

    @Override
    public void render(SpriteBatch sb) {
        grid.tryRender(sb);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        grid.tryUpdate();

        if (InputHelper.justClickedLeft && !grid.isHovered()) {
            complete();
        }
    }
}