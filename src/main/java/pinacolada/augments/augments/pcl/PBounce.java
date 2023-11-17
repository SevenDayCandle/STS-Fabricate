package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PBounce extends PCLAugment {
    public static final PCLAugmentData DATA = register(PBounce.class, PCLAugmentCategory.Played)
            .setMaxUpgrades(1)
            .setTier(2, 1);

    public PBounce(SaveData save) {
        super(DATA, save);
    }

    public void setup() {
        addUseMove(PTrait.tags(1, PCLCardTag.Bounce).setUpgrade(1), PTrait.tags(0, PCLCardTag.Ethereal).setUpgrade(1));
    }
}
