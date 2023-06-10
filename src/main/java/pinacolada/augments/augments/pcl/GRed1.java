package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrait;

@VisibleAugment
public class GRed1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(GRed1.class, PCLAugmentCategorySub.AffinityRed, 1)
            .setSkill(PTrait.affinity(PCLAffinity.Red));

    public GRed1() {
        super(DATA);
    }

    public GRed1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
