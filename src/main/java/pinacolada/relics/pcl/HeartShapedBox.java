package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleRelic;
import pinacolada.interfaces.providers.CardRewardBonusProvider;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.ui.cardReward.PCLCardRewardBundle;
import pinacolada.utilities.GameUtilities;

@VisibleRelic
public class HeartShapedBox extends PCLRelic implements CardRewardBonusProvider {
    public static final PCLRelicData DATA = registerTemplate(HeartShapedBox.class)
            .setTier(RelicTier.SPECIAL)
            .setLoadoutValue(10)
            .setUnique(true);
    public static final int BASE_CHANCE = 1;
    public static final int BASE_OFFSET = 50;
    public static final int REWARD_GOLD = 11;
    public static final int REWARD_HP = 1;

    public HeartShapedBox() {
        super(DATA);
    }

    public static int getChance() {
        return GameUtilities.getTotalCardsInRewardPool() - BASE_OFFSET;
    }

    @Override
    public PCLCardRewardBundle getBundle(AbstractCard card) {
        float chance = getChance();
        if (GameUtilities.chance(chance)) {
            int goldVal = REWARD_GOLD;
            int hpVal = REWARD_HP;
            while (chance > 100) {
                chance -= 100;
                if (GameUtilities.chance(chance)) {
                    goldVal += GameUtilities.getRNG().random(REWARD_GOLD / 2, REWARD_GOLD);
                    hpVal += GameUtilities.getRNG().random(REWARD_HP / 2, REWARD_HP);
                }
            }

            int value = GameUtilities.getRNG().random(0, 9);
            switch (value) {
                case 1:
                case 2:
                case 3:
                    return CardRewardBonusProvider.getMaxHPBundle(card, REWARD_HP);
                default:
                    return CardRewardBonusProvider.getGoldBundle(card, REWARD_GOLD);
            }
        }

        return null;
    }

    public String getDescriptionImpl() {
        if (GameUtilities.inGame()) {
            return EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, formatDescription(0, BASE_OFFSET), formatDescription(1, getChance()));
        }
        return formatDescription(0, BASE_OFFSET);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        super.onEnterRoom(room);

        updateDescription(null);
    }
}
