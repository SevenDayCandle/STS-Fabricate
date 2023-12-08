package pinacolada.interfaces.providers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.card.PermanentUpgradeEffect;
import pinacolada.resources.PGR;
import pinacolada.ui.cardReward.PCLCardRewardBundle;
import pinacolada.utilities.GameUtilities;

public interface CardRewardBonusProvider {
    static PCLCardRewardBundle getGoldBundle(AbstractCard card, int gold) {
        return new PCLCardRewardBundle(card, CardRewardBonusProvider::receiveGold).setAmount(gold)
                .setIcon(ImageMaster.UI_GOLD, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.55f)
                .setText(PGR.core.strings.rewards_goldBonus(gold), Color.WHITE, -AbstractCard.RAW_W * 0.165f, -AbstractCard.RAW_H * 0.54f);
    }

    static PCLCardRewardBundle getMaxHPBundle(AbstractCard card, int maxHP) {
        return new PCLCardRewardBundle(card, CardRewardBonusProvider::receiveMaxHP).setAmount(maxHP)
                .setIcon(ImageMaster.TP_HP, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.55f)
                .setText(PGR.core.strings.rewards_maxHPBonus(maxHP), Color.WHITE, -AbstractCard.RAW_W * 0.165f, -AbstractCard.RAW_H * 0.54f);
    }

    static PCLCardRewardBundle getPotionBundle(AbstractCard card, AbstractPotion potion) {
        return new PCLCardRewardBundle(card, (__) -> CardRewardBonusProvider.receivePotion(potion))
                .setIcon(ImageMaster.POTION_S_CONTAINER, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.55f)
                .setText(potion.name, Color.WHITE, -AbstractCard.RAW_W * 0.165f, -AbstractCard.RAW_H * 0.54f);
    }

    static PCLCardRewardBundle getRelicBundle(AbstractCard card, AbstractRelic relic) {
        return new PCLCardRewardBundle(card, (__) -> GameUtilities.obtainRelicFromEvent(relic))
                .setIcon(relic.img, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.55f)
                .setText(relic.name, Color.WHITE, -AbstractCard.RAW_W * 0.165f, -AbstractCard.RAW_H * 0.54f);
    }

    static void receiveGold(PCLCardRewardBundle bundle) {
        PCLSFX.play(PCLSFX.GOLD_GAIN);
        AbstractDungeon.player.gainGold(bundle.amount);
    }

    static void receiveMaxHP(PCLCardRewardBundle bundle) {
        AbstractDungeon.player.increaseMaxHp(bundle.amount, true);
    }

    static void receivePotion(AbstractPotion potion) {
        AbstractDungeon.player.obtainPotion(potion);
    }

    static void receiveUpgrade(PCLCardRewardBundle bundle) {
        PCLEffects.TopLevelQueue.add(new PermanentUpgradeEffect());
    }

    default boolean canActivate(RewardItem rewardItem) {
        return !CombatManager.inBattle() && rewardItem != null && (rewardItem.type == RewardItem.RewardType.CARD);
    }

    PCLCardRewardBundle getBundle(AbstractCard card);
}
