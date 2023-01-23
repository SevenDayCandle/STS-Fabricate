package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class OInnateA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(OInnateA1.class, 1, PCLAffinity.Orange)
            .setSkill(PTrait.hasTags(PCLCardTag.Innate))
            .setReqs(setTagsNot(PCLCardTag.Delayed, PCLCardTag.Innate));

    public OInnateA1()
    {
        super(DATA);
    }

    public OInnateA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
