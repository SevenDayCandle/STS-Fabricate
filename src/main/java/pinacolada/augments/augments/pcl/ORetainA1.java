package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class ORetainA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(ORetainA1.class, 1, PCLAffinity.Orange)
            .setSkill(PTrait.hasTags(PCLCardTag.Retain))
            .setReqs(setTagsNot(PCLCardTag.Retain, PCLCardTag.Ethereal, PCLCardTag.Fragile));

    public ORetainA1()
    {
        super(DATA);
    }

    public ORetainA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
