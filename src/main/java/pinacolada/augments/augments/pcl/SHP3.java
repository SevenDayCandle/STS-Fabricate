package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrait;

@VisibleAugment
public class SHP3 extends PCLAugment {
    public static final PCLAugmentData DATA = register(SHP3.class, PCLAugmentCategorySub.HP, 3)
            .setSkill(PTrait.hp(12), PTrait.damage(-2));

    public SHP3() {
        super(DATA);
    }

    public SHP3(PSkill<?> skill) {
        super(DATA, skill);
    }
}
