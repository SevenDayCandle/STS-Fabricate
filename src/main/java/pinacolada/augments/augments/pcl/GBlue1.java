package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrait;

@VisibleAugment
public class GBlue1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(GBlue1.class, PCLAugmentCategorySub.AffinityBlue, 1)
            .setSkill(PTrait.affinity(PCLAffinity.Blue));

    public GBlue1() {
        super(DATA);
    }

    public GBlue1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
