package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class ORetainA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(ORetainA2.class, 3, PCLAffinity.Orange)
            .setSkill(PTrait.tags(-1, PCLCardTag.Retain, PCLCardTag.Grave))
            .setReqs(setTagsNot(PCLCardTag.Retain, PCLCardTag.Ethereal, PCLCardTag.Fragile));

    public ORetainA2()
    {
        super(DATA);
    }

    public ORetainA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
