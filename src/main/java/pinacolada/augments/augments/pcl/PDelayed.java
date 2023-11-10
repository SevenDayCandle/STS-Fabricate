package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PDelayed extends PCLAugment {
    public static final PCLAugmentData DATA = register(PDelayed.class, PCLAugmentCategory.Played)
            .setMaxUpgrades(1);

    public PDelayed(SaveData save) {
        super(DATA, save);
    }

    public void setup() {
        addUseMove(PTrait.tagsExact(1, PCLCardTag.Delayed).setUpgrade(-2));
    }
}
