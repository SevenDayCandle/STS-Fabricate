package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrait;

@VisibleAugment
public class PDelayed2 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PDelayed2.class, PCLAugmentCategorySub.TagDelayed, 3)
            .setSkill(PTrait.tags(-1, PCLCardTag.Delayed));

    public PDelayed2() {
        super(DATA);
    }

    public PDelayed2(PSkill<?> skill) {
        super(DATA, skill);
    }
}
