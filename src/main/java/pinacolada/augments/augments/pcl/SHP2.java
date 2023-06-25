package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class SHP2 extends PCLAugment {
    public static final PCLAugmentData DATA = register(SHP2.class, PCLAugmentCategorySub.HP, 2)
            .setSkill(PTrait.hp(6), PTrait.damage(-1));

    public SHP2() {
        super(DATA);
    }

    public SHP2(PSkill<?> skill) {
        super(DATA, skill);
    }
}
