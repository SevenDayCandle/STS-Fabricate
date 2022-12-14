package pinacolada.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import pinacolada.utilities.GameUtilities;

public interface CardRewardActionProvider
{
    boolean canAct();

    default boolean canActivate(RewardItem rewardItem)
    {
        return canAct() && !GameUtilities.inBattle() && rewardItem != null && (rewardItem.type == RewardItem.RewardType.CARD);
    }

    AbstractCard doAction(AbstractCard card, RewardItem rewardItem, int cardIndex);
}
