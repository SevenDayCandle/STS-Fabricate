package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrait;

@VisibleAugment
public class SDamageCount1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(SDamageCount1.class, PCLAugmentCategorySub.DamageCount, 1)
            .setSkill(PTrait.hitCount(1), PTrait.damageMultiplier(-45));

    public SDamageCount1() {
        super(DATA);
    }

    public SDamageCount1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
