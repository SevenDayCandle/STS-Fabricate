package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class GBlue2 extends PCLAugment {
    public static final PCLAugmentData DATA = register(GBlue2.class, PCLAugmentCategorySub.AffinityBlue, 3)
            .setSkill(PTrait.affinity(2, PCLAffinity.Blue));

    public GBlue2() {
        super(DATA);
    }

    public GBlue2(PSkill<?> skill) {
        super(DATA, skill);
    }
}
