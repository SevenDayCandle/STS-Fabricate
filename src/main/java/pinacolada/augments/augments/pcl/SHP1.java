package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class SHP1 extends PCLAugment
{
    public static final PCLAugmentData DATA = register(SHP1.class, PCLAugmentCategorySub.HP, 1)
            .setSkill(PTrait.hp(2));

    public SHP1()
    {
        super(DATA);
    }

    public SHP1(PSkill<?> skill)
    {
        super(DATA, skill);
    }
}
