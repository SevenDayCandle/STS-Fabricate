package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.interfaces.markers.CardRewardActionProvider;
import pinacolada.relics.pcl.MagicEraser;
import pinacolada.resources.PGR;

public class PCLCardRewardBreakAction extends PCLCardRewardAction
{
    protected static final float REWARD_INDEX = AbstractCard.IMG_HEIGHT * 0.515f;

    public PCLCardRewardBreakAction(ActionT1<AbstractCard> onCardAdded, ActionT1<AbstractCard> onCardReroll)
    {
        super(onCardAdded, onCardReroll);
    }

    public AbstractCard actionImpl(PCLCardRewardActionButton button, AbstractCard card, int cardIndex)
    {
        AbstractCard target = actionProvider.doAction(card, rewardItem, cardIndex);
        PCLAugment augment = button.getAugment();
        if (augment != null) {
            PGR.core.dungeon.addAugment(augment.ID, 1);
        }
        takeReward();

        return card;
    }

    @Override
    PCLCardRewardActionButton getButton(int index)
    {
        return (PCLCardRewardActionButton) new PCLCardRewardActionButton(this,
                EUIRM.images.hexagonalButton.texture(), PGR.core.strings.rewards.rewardBreak, PGR.core.strings.rewards.breakDescription, REWARD_INDEX, index, true)
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setDimensions(AbstractCard.IMG_WIDTH * 0.75f, AbstractCard.IMG_HEIGHT * 0.14f)
                .setColor(new Color(0.8f, 0.2f, 0.2f, 1f))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Settings.GOLD_COLOR);
    }

    @Override
    Class<? extends CardRewardActionProvider> getTargetClass()
    {
        return MagicEraser.class;
    }
}
