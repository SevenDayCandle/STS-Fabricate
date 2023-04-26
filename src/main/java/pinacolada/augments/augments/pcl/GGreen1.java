package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class GGreen1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(GGreen1.class, PCLAugmentCategorySub.AffinityGreen, 1)
            .setSkill(PTrait.affinity(PCLAffinity.Green));

    public GGreen1() {
        super(DATA);
    }

    public GGreen1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
