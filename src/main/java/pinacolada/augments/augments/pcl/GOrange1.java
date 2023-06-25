package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class GOrange1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(GOrange1.class, PCLAugmentCategorySub.AffinityOrange, 1)
            .setSkill(PTrait.affinity(PCLAffinity.Orange));

    public GOrange1() {
        super(DATA);
    }

    public GOrange1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
