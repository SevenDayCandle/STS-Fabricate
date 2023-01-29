package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class BBlueA1 extends PCLAugment
{
    public static final PCLAugmentData DATA = register(BBlueA1.class, 1, PCLAffinity.Blue)
            .setSkill(PTrait.affinity(PCLAffinity.Blue))
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
