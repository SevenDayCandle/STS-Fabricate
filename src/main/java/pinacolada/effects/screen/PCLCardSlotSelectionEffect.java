package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRM;
import extendedui.ui.controls.EUICardGrid;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffect;
import pinacolada.resources.loadout.LoadoutCardSlot;
import pinacolada.ui.characterSelection.PCLCardSlotEditor;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLCardSlotSelectionEffect extends PCLEffect {
    private static final EUITextBox cardValue_text = new
            EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(AbstractCard.IMG_WIDTH * 0.6f, AbstractCard.IMG_HEIGHT * 0.15f))
            .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
            .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
            .setAlignment(0.5f, 0.5f)
            .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

    private final PCLCardSlotEditor slot;
    private final boolean draggingScreen = false;
    private AbstractCard selectedCard;
    private EUICardGrid grid;

    public PCLCardSlotSelectionEffect(PCLCardSlotEditor slot) {
        super(0.7f, true);

        this.selectedCard = slot.slot.getCard(false);
        this.slot = slot;
        ArrayList<LoadoutCardSlot.Item> cards = slot.getSelectableCards();

        if (cards.isEmpty()) {
            complete();
            return;
        }

        this.grid = new EUICardGrid()
                .addPadY(AbstractCard.IMG_HEIGHT * 0.15f)
                .setEnlargeOnHover(false)
                .setOnCardClick(this::onCardClicked)
                .setOnCardRender(this::onCardRender);

        for (LoadoutCardSlot.Item item : cards) {
            AbstractCard card = item.getCard(true);
            card.current_x = InputHelper.mX;
            card.current_y = InputHelper.mY;
            grid.addCard(card);
        }
    }

    @Override
    protected void complete() {
        super.complete();

        if (selectedCard != null && !selectedCard.cardID.equals(slot.slot.getSelectedID())) {
            slot.slot.select(selectedCard.cardID, 1);
        }
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

    private void onCardClicked(AbstractCard card) {
        selectedCard = card;
        CardCrawlGame.sound.play("CARD_SELECT");
        slot.slot.select(card.cardID, 1);
        card.beginGlowing();
        complete();
    }

    private void onCardRender(SpriteBatch sb, AbstractCard card) {
        for (LoadoutCardSlot.Item item : slot.slot.cards) {
            if (item.ID.equals(card.cardID)) {
                cardValue_text
                        .setLabel(item.estimatedValue)
                        .setFontColor(item.estimatedValue < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR)
                        .setPosition(card.hb.cX, card.hb.cY - (card.hb.height * 0.65f))
                        .renderImpl(sb);
                return;
            }
        }
    }
}