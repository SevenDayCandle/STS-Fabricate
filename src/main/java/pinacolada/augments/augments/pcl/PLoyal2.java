package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PLoyal2 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PLoyal2.class, PCLAugmentCategorySub.TagLoyal, 3)
            .setSkill(PTrait.tags(-1, PCLCardTag.Loyal), PTrait.tags(PCLCardTag.Ethereal));

    public PLoyal2() {
        super(DATA);
    }

    public PLoyal2(PSkill<?> skill) {
        super(DATA, skill);
    }
}
