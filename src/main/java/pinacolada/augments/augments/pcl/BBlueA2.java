package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;


@VisibleAugment
public class BBlueA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(BBlueA2.class, 3, PCLAffinity.Blue)
            .setSkill(PTrait.affinity(2, PCLAffinity.Blue), PTrait.affinityNot(PCLAffinity.Red, PCLAffinity.Green, PCLAffinity.Orange))
            .setReqs(setAffinitiesNot(PCLAffinity.Blue));

    public BBlueA2()
    {
        super(DATA);
    }

    public BBlueA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
