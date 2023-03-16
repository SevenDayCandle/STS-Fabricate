package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;
import pinacolada.effects.card.HideCardEffect;
import pinacolada.interfaces.providers.CardRewardActionProvider;
import pinacolada.relics.pcl.AbstractCubes;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class PCLCardRewardRerollAction extends PCLCardRewardAction
{
    protected static final float REWARD_INDEX = AbstractCard.IMG_HEIGHT * 0.515f;

    public PCLCardRewardRerollAction(ActionT1<AbstractCard> onCardAdded, ActionT1<AbstractCard> onCardReroll)
    {
        super(onCardAdded, onCardReroll);
    }

    public AbstractCard actionImpl(PCLCardRewardActionButton button, AbstractCard card, int cardIndex)
    {
        final AbstractCard replacement = actionProvider.doAction(card, rewardItem, cardIndex);
        if (replacement != null)
        {
            SFX.play(SFX.CARD_SELECT);
            PCLEffects.TopLevelList.add(new ExhaustCardEffect(card));
            PCLEffects.TopLevelList.add(new HideCardEffect(card));
            GameUtilities.copyVisualProperties(replacement, card);
            rewardItem.cards.set(cardIndex, replacement);
        }
        return replacement;
    }

    @Override
    PCLCardRewardActionButton getButton(int index)
    {
        return (PCLCardRewardActionButton) new PCLCardRewardActionButton(this,
                EUIRM.images.hexagonalButton.texture(), PGR.core.strings.rewards_reroll, PGR.core.strings.rewards_rerollDescription, REWARD_INDEX, index, false)
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setDimensions(AbstractCard.IMG_WIDTH * 0.75f, AbstractCard.IMG_HEIGHT * 0.14f)
                .setColor(Color.TAN)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Settings.GOLD_COLOR);
    }

    @Override
    Class<? extends CardRewardActionProvider> getTargetClass()
    {
        return AbstractCubes.class;
    }
}
