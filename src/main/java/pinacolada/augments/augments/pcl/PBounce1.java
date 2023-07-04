package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PBounce1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PBounce1.class, PCLAugmentCategorySub.TagBounce, 1)
            .setSkill(PTrait.tags(PCLCardTag.Bounce));

    public PBounce1() {
        super(DATA);
    }

    public PBounce1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
