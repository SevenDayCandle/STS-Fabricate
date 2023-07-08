package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.interfaces.providers.CardRewardBonusProvider;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.ui.cardReward.PCLCardRewardBundle;
import pinacolada.utilities.GameUtilities;

public class HeartShapedBox extends PCLRelic implements CardRewardBonusProvider {
    public static final PCLRelicData DATA = registerTemplate(HeartShapedBox.class)
            .setTier(RelicTier.SPECIAL);
    public static final float BASE_CHANCE = 0.3f;
    public static final int BASE_GOLD = 10;
    public static final int BASE_HP = 1;

    public HeartShapedBox() {
        super(DATA);
    }

    @Override
    public PCLCardRewardBundle getBundle(AbstractCard card) {
        float chance = BASE_CHANCE / (1 + GameUtilities.getMasterDeckCopies(card.cardID).size());
        if (GameUtilities.getRNG().randomBoolean(chance)) {
            int value = GameUtilities.getRNG().random(0, 9);
            switch (value) {
                case 1:
                case 2:
                case 3:
                    return CardRewardBonusProvider.getMaxHPBundle(card, BASE_HP);
                default:
                    return CardRewardBonusProvider.getGoldBundle(card, BASE_GOLD);
            }
        }

        return null;
    }
}
