package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrait;

@VisibleAugment
public class PInnate1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PInnate1.class, PCLAugmentCategorySub.TagInnate, 1)
            .setSkill(PTrait.tags(PCLCardTag.Innate));

    public PInnate1() {
        super(DATA);
    }

    public PInnate1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
