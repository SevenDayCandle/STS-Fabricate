package pinacolada.augments.augments.pcl;

import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class ODelayA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(ODelayA2.class, 3, PCLAffinity.Orange)
            .setSkill(PTrait.hasTags(-1, PCLCardTag.Delayed))
            .setReqs(setTagsNot(PCLCardTag.Innate));

    public ODelayA2()
    {
        super(DATA);
    }

    public ODelayA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
