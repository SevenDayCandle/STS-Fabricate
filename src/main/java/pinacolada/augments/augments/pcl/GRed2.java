package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class GRed2 extends PCLAugment
{
    public static final PCLAugmentData DATA = register(GRed2.class, PCLAugmentCategorySub.AffinityRed, 3)
            .setSkill(PTrait.affinity(2, PCLAffinity.Red));

    public GRed2()
    {
        super(DATA);
    }

    public GRed2(PSkill<?> skill)
    {
        super(DATA, skill);
    }
}
