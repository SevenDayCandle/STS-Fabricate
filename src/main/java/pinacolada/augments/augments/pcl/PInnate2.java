package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrait;

@VisibleAugment
public class PInnate2 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PInnate2.class, PCLAugmentCategorySub.TagInnate, 3)
            .setSkill(PTrait.tags(-1, PCLCardTag.Innate), PTrait.tags(PCLCardTag.Ethereal));

    public PInnate2() {
        super(DATA);
    }

    public PInnate2(PSkill<?> skill) {
        super(DATA, skill);
    }
}
