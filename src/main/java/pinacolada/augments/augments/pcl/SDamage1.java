package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class SDamage1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(SDamage1.class, PCLAugmentCategorySub.Damage, 1)
            .setSkill(PTrait.damage(1));

    public SDamage1() {
        super(DATA);
    }

    public SDamage1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
