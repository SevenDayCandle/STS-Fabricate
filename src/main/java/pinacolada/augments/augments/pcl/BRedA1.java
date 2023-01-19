package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class BRedA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(BRedA1.class, 1, PCLAffinity.Blue)
            .setSkill(PTrait.hasAffinity(PCLAffinity.Red))
            .setReqs(setAffinitiesNot(PCLAffinity.Red));

    public BRedA1()
    {
        super(DATA);
    }

    public BRedA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
