package pinacolada.interfaces.providers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.card.HideCardEffect;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public interface CardRewardActionProvider {
    default boolean canActivate(RewardItem rewardItem) {
        return canAct() && !GameUtilities.inBattle() && rewardItem != null && (rewardItem.type == RewardItem.RewardType.CARD);
    }

    default String getDescription() {
        return PGR.core.strings.rewards_rerollDescription;
    }

    default String getTitle() {
        return PGR.core.strings.rewards_reroll;
    }

    default void rerollCard(AbstractCard card, AbstractCard replacement, RewardItem rewardItem, int cardIndex) {
        if (replacement != null) {
            PCLSFX.play(PCLSFX.CARD_SELECT);
            PCLEffects.TopLevelList.add(new ExhaustCardEffect(card));
            PCLEffects.TopLevelList.add(new HideCardEffect(card));
            GameUtilities.copyVisualProperties(replacement, card);
            rewardItem.cards.set(cardIndex, replacement);
        }
    }

    boolean canAct();
    boolean doAction(AbstractCard card, RewardItem rewardItem, int cardIndex);
}
