package pinacolada.augments.augments.pcl;

import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;
import pinacolada.skills.skills.PMultiTrait;

public class OInnateA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(OInnateA2.class, 3, PCLAffinity.Orange)
            .setSkill(PMultiTrait.join(PTrait.hasTags(-1, PCLCardTag.Innate), PTrait.hasTags(1, PCLCardTag.Haste)))
            .setReqs(setTagsNot(PCLCardTag.Delayed));

    public OInnateA2()
    {
        super(DATA);
    }

    public OInnateA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
