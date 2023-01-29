package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class OHasteA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(OHasteA1.class, 2, PCLAffinity.Orange)
            .setSkill(PTrait.tags(PCLCardTag.Haste, PCLCardTag.Ethereal))
            .setReqs(setTagsNot(PCLCardTag.Haste, PCLCardTag.Ethereal));

    public OHasteA1()
    {
        super(DATA);
    }

    public OHasteA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
