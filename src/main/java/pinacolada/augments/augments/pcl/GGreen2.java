package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrait;

@VisibleAugment
public class GGreen2 extends PCLAugment {
    public static final PCLAugmentData DATA = register(GGreen2.class, PCLAugmentCategorySub.AffinityGreen, 3)
            .setSkill(PTrait.affinity(2, PCLAffinity.Green));

    public GGreen2() {
        super(DATA);
    }

    public GGreen2(PSkill<?> skill) {
        super(DATA, skill);
    }
}
