package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PRetain1 extends PCLAugment
{
    public static final PCLAugmentData DATA = register(PRetain1.class, PCLAugmentCategorySub.TagRetain, 1)
            .setSkill(PTrait.tags(PCLCardTag.Innate));

    public PRetain1()
    {
        super(DATA);
    }

    public PRetain1(PSkill<?> skill)
    {
        super(DATA, skill);
    }
}
