package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PTrait;

@VisibleAugment
public class GGreen extends PCLAugment {
    public static final PCLAugmentData DATA = register(GGreen.class, PCLAugmentCategory.General);

    public GGreen(int timesUpgraded, int level) {
        super(DATA);
    }

    public void setup() {
        addUseMove(PTrait.affinity(PCLAffinity.Green));
    }
}
