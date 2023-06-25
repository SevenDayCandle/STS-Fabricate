package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PExhaust1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PExhaust1.class, PCLAugmentCategorySub.TagExhaust, 3)
            .setSkill(PTrait.cost(-1), PTrait.tags(PCLCardTag.Exhaust));

    public PExhaust1() {
        super(DATA);
    }

    public PExhaust1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
