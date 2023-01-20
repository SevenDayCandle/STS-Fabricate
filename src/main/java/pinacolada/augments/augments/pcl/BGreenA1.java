package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;


@VisibleAugment
public class BGreenA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(BGreenA1.class, 1, PCLAffinity.Blue)
            .setSkill(PTrait.hasAffinity(PCLAffinity.Green))
            .setReqs(setAffinitiesNot(PCLAffinity.Green));

    public BGreenA1()
    {
        super(DATA);
    }

    public BGreenA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
