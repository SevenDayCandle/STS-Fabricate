package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrait;

@VisibleAugment
public class PDelayed1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PDelayed1.class, PCLAugmentCategorySub.TagDelayed, 1)
            .setSkill(PTrait.tags(PCLCardTag.Delayed));

    public PDelayed1() {
        super(DATA);
    }

    public PDelayed1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
