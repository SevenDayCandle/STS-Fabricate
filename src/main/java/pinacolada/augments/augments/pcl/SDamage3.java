package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class SDamage3 extends PCLAugment {
    public static final PCLAugmentData DATA = register(SDamage3.class, PCLAugmentCategorySub.Damage, 3)
            .setSkill(PTrait.damage(4), PTrait.hp(-2), PTrait.cost(1));

    public SDamage3() {
        super(DATA);
    }

    public SDamage3(PSkill<?> skill) {
        super(DATA, skill);
    }
}
