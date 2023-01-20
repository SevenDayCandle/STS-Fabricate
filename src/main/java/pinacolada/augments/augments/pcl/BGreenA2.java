package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;


@VisibleAugment
public class BGreenA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(BGreenA2.class, 3, PCLAffinity.Blue)
            .setSkill(PTrait.hasAffinity(2, PCLAffinity.Green), PTrait.hasAffinityNot(PCLAffinity.Red, PCLAffinity.Blue, PCLAffinity.Orange))
            .setReqs(setAffinitiesNot(PCLAffinity.Green));

    public BGreenA2()
    {
        super(DATA);
    }

    public BGreenA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
