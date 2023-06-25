package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class SDamageCount3 extends PCLAugment {
    public static final PCLAugmentData DATA = register(SDamageCount3.class, PCLAugmentCategorySub.DamageCount, 3)
            .setSkill(PTrait.hitCount(3), PTrait.damageMultiplier(-75));

    public SDamageCount3() {
        super(DATA);
    }

    public SDamageCount3(PSkill<?> skill) {
        super(DATA, skill);
    }
}
