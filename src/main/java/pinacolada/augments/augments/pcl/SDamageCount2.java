package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class SDamageCount2 extends PCLAugment {
    public static final PCLAugmentData DATA = register(SDamageCount2.class, PCLAugmentCategorySub.DamageCount, 2)
            .setSkill(PTrait.hitCount(2), PTrait.damageMultiplier(-65));

    public SDamageCount2() {
        super(DATA);
    }

    public SDamageCount2(PSkill<?> skill) {
        super(DATA, skill);
    }
}
