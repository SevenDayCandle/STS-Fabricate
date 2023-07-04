package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PRecast1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PRecast1.class, PCLAugmentCategorySub.TagRecast, 3)
            .setSkill(PTrait.tags(1, PCLCardTag.Recast), PTrait.tags(PCLCardTag.Ethereal));

    public PRecast1() {
        super(DATA);
    }

    public PRecast1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
