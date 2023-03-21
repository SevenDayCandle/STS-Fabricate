package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PHaste2 extends PCLAugment
{
    public static final PCLAugmentData DATA = register(PHaste2.class, PCLAugmentCategorySub.TagHaste, 3)
            .setSkill(PTrait.tags(-1, PCLCardTag.Haste), PTrait.tags(PCLCardTag.Ethereal));

    public PHaste2()
    {
        super(DATA);
    }

    public PHaste2(PSkill<?> skill)
    {
        super(DATA, skill);
    }
}
