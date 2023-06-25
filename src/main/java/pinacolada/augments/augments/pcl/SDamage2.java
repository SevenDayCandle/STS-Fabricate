package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class SDamage2 extends PCLAugment {
    public static final PCLAugmentData DATA = register(SDamage2.class, PCLAugmentCategorySub.Damage, 2)
            .setSkill(PTrait.damage(2), PTrait.hp(-2));

    public SDamage2() {
        super(DATA);
    }

    public SDamage2(PSkill<?> skill) {
        super(DATA, skill);
    }
}
