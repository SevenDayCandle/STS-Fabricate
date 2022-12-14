package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRenderHelpers;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.augments.PCLAugmentWeights;
import pinacolada.resources.PGR;

public class PCLCardRewardActionButton extends EUIButton
{
    private static final float SIZE = scale(50);
    private final float offsetY;
    private final int cardIndex;
    public PCLCardRewardAction container;
    public boolean used;
    private AbstractCard card;
    private PCLAugment augment;
    private EUIImage augmentImage;
    private boolean useAugment;

    public PCLCardRewardActionButton(PCLCardRewardAction container, Texture buttonTexture, String title, String description, float offsetY, int cardIndex, boolean useAugment)
    {
        super(buttonTexture, 0, 0);

        this.augmentImage = new EUIImage(PGR.core.images.augments.augment.texture(), new RelativeHitbox(hb, SIZE, SIZE, hb.width * 0.7f, hb.height * 0.3f));
        this.augmentImage.setActive(useAugment);
        this.useAugment = useAugment;
        this.container = container;
        this.cardIndex = cardIndex;
        this.card = getCard(true);
        if (useAugment) {
            updateAugment();
        }
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
        AbstractCard newCard = getCard(false);
        if (card != newCard && useAugment) {
            updateAugment();
        }
        card = newCard;

        if (card != null)
        {
            hb.targetCx = card.current_x;
            hb.targetCy = card.current_y + offsetY;
        }

        setInteractable(card != null && !card.hb.hovered);

        this.augmentImage.tryUpdate();
        super.updateImpl();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        if (card != null)
        {
            super.renderImpl(sb);
            this.augmentImage.tryRender(sb);
        }
    }

    public PCLAugment getAugment() {
        return augment;
    }

    protected void updateAugment() {
        if (this.card != null) {
            PCLAugmentWeights weights = new PCLAugmentWeights(this.card);
            PCLAugmentData data = PCLAugment.getWeighted(PGR.core.dungeon.getRNG(), weights);
            if (data != null) {
                augment = data.create();
                augmentImage.setTexture(augment.getTexture())
                        .setColor(augment.getColor())
                        .setShaderMode(EUIRenderHelpers.ShaderMode.Colorize)
                        .setTooltip(augment.getTip())
                        .setActive(true);
            }
        }
    }
}