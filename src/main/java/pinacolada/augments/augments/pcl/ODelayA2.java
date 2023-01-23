package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
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
