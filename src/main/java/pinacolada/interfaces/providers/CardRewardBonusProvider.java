package pinacolada.interfaces.providers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rewards.RewardItem;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.card.PermanentUpgradeEffect;
import pinacolada.resources.PGR;
import pinacolada.ui.cardReward.CardRewardBundle;
import pinacolada.utilities.GameUtilities;

public interface CardRewardBonusProvider {
    static CardRewardBundle getGoldBundle(AbstractCard card, int gold) {
        return new CardRewardBundle(card, CardRewardBonusProvider::receiveGold).setAmount(gold)
                .setIcon(ImageMaster.UI_GOLD, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.545f)
                .setText(PGR.core.strings.rewards_goldBonus(gold), Color.WHITE, -AbstractCard.RAW_W * 0.165f, -AbstractCard.RAW_H * 0.54f);
    }

    static CardRewardBundle getMaxHPBundle(AbstractCard card, int maxHP) {
        return new CardRewardBundle(card, CardRewardBonusProvider::receiveMaxHP).setAmount(maxHP)
                .setIcon(ImageMaster.TP_HP, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.545f)
                .setText(PGR.core.strings.rewards_maxHPBonus(maxHP), Color.WHITE, -AbstractCard.RAW_W * 0.165f, -AbstractCard.RAW_H * 0.54f);
    }

    static void receiveGold(CardRewardBundle bundle) {
        PCLSFX.play(PCLSFX.GOLD_GAIN);
        AbstractDungeon.player.gainGold(bundle.amount);
    }

    static void receiveMaxHP(CardRewardBundle bundle) {
        AbstractDungeon.player.increaseMaxHp(bundle.amount, true);
    }

    static void receiveUpgrade(CardRewardBundle bundle) {
        PCLEffects.TopLevelQueue.add(new PermanentUpgradeEffect());
    }

    default boolean canActivate(RewardItem rewardItem) {
        return !GameUtilities.inBattle() && rewardItem != null && (rewardItem.type == RewardItem.RewardType.CARD);
    }

    CardRewardBundle getBundle(AbstractCard card);
}
