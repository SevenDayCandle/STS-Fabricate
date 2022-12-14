package pinacolada.augments.augments.pcl;

import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class ORetainA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(ORetainA2.class, 3, PCLAffinity.Orange)
            .setSkill(PTrait.hasTags(-1, PCLCardTag.Retain, PCLCardTag.Grave))
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
