package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;


@VisibleAugment
public class BRedA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(BRedA2.class, 3, PCLAffinity.Blue)
            .setSkill(PTrait.affinity(2, PCLAffinity.Red), PTrait.affinityNot(PCLAffinity.Green, PCLAffinity.Blue, PCLAffinity.Orange))
            .setReqs(setAffinitiesNot(PCLAffinity.Red));

    public BRedA2()
    {
        super(DATA);
    }

    public BRedA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
