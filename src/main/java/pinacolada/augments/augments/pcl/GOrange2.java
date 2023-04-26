package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class GOrange2 extends PCLAugment {
    public static final PCLAugmentData DATA = register(GOrange2.class, PCLAugmentCategorySub.AffinityOrange, 3)
            .setSkill(PTrait.affinity(2, PCLAffinity.Orange));

    public GOrange2() {
        super(DATA);
    }

    public GOrange2(PSkill<?> skill) {
        super(DATA, skill);
    }
}
