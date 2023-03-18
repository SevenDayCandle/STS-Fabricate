package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.tooltips.EUITooltip;

// Copied and modified from STS-AnimatorMod
public class PCLCardRewardActionButton extends EUIButton
{
    private static final float SIZE = scale(50);
    private final float offsetY;
    private final int cardIndex;
    public PCLCardRewardAction container;
    public boolean used;
    private AbstractCard card;

    public PCLCardRewardActionButton(PCLCardRewardAction container, Texture buttonTexture, String title, String description, float offsetY, int cardIndex, boolean useAugment)
    {
        super(buttonTexture, 0, 0);

        this.container = container;
        this.cardIndex = cardIndex;
        this.card = getCard(true);
        this.offsetY = offsetY;

        setOnClick(() -> this.container.action(this));
        setPosition(card.current_x, card.current_y + offsetY);
        setText(title);
        setTooltip(new EUITooltip(title, description));
    }

    public AbstractCard getCard(boolean includeColorless)
    {
        if (cardIndex < container.rewardItem.cards.size())
        {
            final AbstractCard card = container.rewardItem.cards.get(cardIndex);
            return (!includeColorless && card.color == AbstractCard.CardColor.COLORLESS) ? null : card;
        }

        return null;
    }

    public int getIndex()
    {
        return cardIndex;
    }

    @Override
    public void updateImpl()
    {
        card = getCard(false);

        if (card != null)
        {
            hb.targetCx = card.current_x;
            hb.targetCy = card.current_y + offsetY;
        }

        setInteractable(card != null && !card.hb.hovered);

        super.updateImpl();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        if (card != null)
        {
            super.renderImpl(sb);
        }
    }
}