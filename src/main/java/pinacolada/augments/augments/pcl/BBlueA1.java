package pinacolada.augments.augments.pcl;

import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class BBlueA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(BBlueA1.class, 1, PCLAffinity.Blue)
            .setSkill(PTrait.hasAffinity(PCLAffinity.Blue))
            .setReqs(setAffinitiesNot(PCLAffinity.Blue));

    public BBlueA1()
    {
        super(DATA);
    }

    public BBlueA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
