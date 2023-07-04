package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PBounce2 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PBounce2.class, PCLAugmentCategorySub.TagBounce, 2)
            .setSkill(PTrait.tags(2, PCLCardTag.Bounce), PTrait.tags(PCLCardTag.Ethereal));

    public PBounce2() {
        super(DATA);
    }

    public PBounce2(PSkill<?> skill) {
        super(DATA, skill);
    }
}
