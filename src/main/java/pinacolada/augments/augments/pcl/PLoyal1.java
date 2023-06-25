package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PLoyal1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PLoyal1.class, PCLAugmentCategorySub.TagLoyal, 1)
            .setSkill(PTrait.tags(PCLCardTag.Loyal));

    public PLoyal1() {
        super(DATA);
    }

    public PLoyal1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
