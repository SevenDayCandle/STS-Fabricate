package pinacolada.ui.characterSelection;

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
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.pcl.PCLCardSlot;

import java.util.ArrayList;

public class PCLCardSlotSelectionEffect extends PCLEffectWithCallback<Object>
{
    private static final EUITextBox cardValue_text = new
            EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(AbstractCard.IMG_WIDTH * 0.6f, AbstractCard.IMG_HEIGHT * 0.15f))
            .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
            .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
            .setAlignment(0.5f, 0.5f)
            .setFont(EUIFontHelper.cardtitlefontSmall, 1f);

    private final PCLCardSlot slot;
    private final ArrayList<PCLCard> cards;
    private final boolean draggingScreen = false;
    private PCLCard selectedCard;
    private EUICardGrid grid;

    public PCLCardSlotSelectionEffect(PCLCardSlot slot)
    {
        super(0.7f, true);

        this.selectedCard = slot.getCard(false);
        this.slot = slot;
        this.cards = slot.getSelectableCards();

        if (cards.isEmpty())
        {
            complete();
            return;
        }

        this.grid = new EUICardGrid()
                .addPadY(AbstractCard.IMG_HEIGHT * 0.15f)
                .setEnlargeOnHover(false)
                .setOnCardClick(c -> onCardClicked((PCLCard) c))
                .setOnCardRender((sb, c) -> onCardRender(sb, (PCLCard) c));

        for (PCLCard card : cards)
        {
            card.current_x = InputHelper.mX;
            card.current_y = InputHelper.mY;
            grid.addCard(card);
        }
    }

    private void onCardClicked(PCLCard card)
    {
        if (card.cardData.isNotSeen())
        {
            CardCrawlGame.sound.play("CARD_REJECT");
        }
        else
        {
            if (selectedCard != null)
            {
                selectedCard.stopGlowing();
            }

            selectedCard = card;
            CardCrawlGame.sound.play("CARD_SELECT");
            slot.select(card.cardData, 1);
            card.beginGlowing();
            complete();
        }
    }

    private void onCardRender(SpriteBatch sb, PCLCard card)
    {
        for (PCLCardSlot.Item item : slot.cards)
        {
            if (item.data == card.cardData)
            {
                cardValue_text
                        .setLabel(item.estimatedValue)
                        .setFontColor(item.estimatedValue < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR)
                        .setPosition(card.hb.cX, card.hb.cY - (card.hb.height * 0.65f))
                        .renderImpl(sb);
                return;
            }
        }
    }

    public PCLCardSlotSelectionEffect setStartingPosition(float x, float y)
    {
        for (AbstractCard c : cards)
        {
            c.current_x = x - (c.hb.width * 0.5f);
            c.current_y = y - (c.hb.height * 0.5f);
        }

        return this;
    }

    @Override
    public void render(SpriteBatch sb)
    {
        grid.tryRender(sb);
    }

    @Override
    protected void firstUpdate()
    {
        super.firstUpdate();

        if (selectedCard != null)
        {
            for (PCLCard card : cards)
            {
                if (card.cardID.equals(selectedCard.cardID))
                {
                    selectedCard = card;
                    selectedCard.beginGlowing();
                    break;
                }
            }
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        grid.tryUpdate();

        if (InputHelper.justClickedLeft && grid.hoveredCard == null)
        {
            complete();
        }
    }

    @Override
    protected void complete()
    {
        super.complete();

        if (selectedCard != null && slot.getData() != selectedCard.cardData)
        {
            slot.select(selectedCard.cardData, 1);
        }
    }
}