package pinacolada.augments.augments.special;

import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class SEtherealEX extends PCLAugment
{

    public static final PCLAugmentData DATA = register(SEtherealEX.class, 5, PCLAffinity.Silver)
            .setSkill(PTrait.hasTags(PCLCardTag.Ethereal), PTrait.hasCost(-1))
            .setReqs(setTagsNot(PCLCardTag.Ethereal, PCLCardTag.Fragile, PCLCardTag.Fleeting))
            .setSpecial(true);

    public SEtherealEX()
    {
        super(DATA);
    }

    public SEtherealEX(PSkill skill)
    {
        super(DATA, skill);
    }
}
