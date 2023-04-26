package pinacolada.interfaces.providers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import pinacolada.utilities.GameUtilities;

public interface CardRewardActionProvider {
    default boolean canActivate(RewardItem rewardItem) {
        return canAct() && !GameUtilities.inBattle() && rewardItem != null && (rewardItem.type == RewardItem.RewardType.CARD);
    }

    boolean canAct();

    AbstractCard doAction(AbstractCard card, RewardItem rewardItem, int cardIndex);
}
