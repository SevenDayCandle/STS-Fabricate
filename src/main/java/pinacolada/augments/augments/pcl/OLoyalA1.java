package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class OLoyalA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(OLoyalA1.class, 3, PCLAffinity.Orange)
            .setSkill(PTrait.hasTags(PCLCardTag.Loyal, PCLCardTag.Ethereal))
            .setReqs(setTagsNot(PCLCardTag.Loyal, PCLCardTag.Ethereal, PCLCardTag.Fragile));

    public OLoyalA1()
    {
        super(DATA);
    }

    public OLoyalA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
