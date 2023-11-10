package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PRetain extends PCLAugment {
    public static final PCLAugmentData DATA = register(PRetain.class, PCLAugmentCategory.Played)
            .setMaxUpgrades(1);

    public PRetain(SaveData save) {
        super(DATA, save);
    }

    public void setup() {
        addUseMove(PTrait.tags(1, PCLCardTag.Retain).setUpgrade(-2));
    }
}
