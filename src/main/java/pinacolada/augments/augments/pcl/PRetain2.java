package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PRetain2 extends PCLAugment
{
    public static final PCLAugmentData DATA = register(PRetain2.class, PCLAugmentCategorySub.TagRetain, 3)
            .setSkill(PTrait.tags(-1, PCLCardTag.Retain), PTrait.tags(PCLCardTag.Grave));

    public PRetain2()
    {
        super(DATA);
    }

    public PRetain2(PSkill<?> skill)
    {
        super(DATA, skill);
    }
}