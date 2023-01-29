package pinacolada.augments.augments.special;

import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class SRecastEX extends PCLAugment
{

    public static final PCLAugmentData DATA = register(SRecastEX.class, 5, PCLAffinity.Silver)
            .setSkill(PTrait.tags(PCLCardTag.Recast))
            .setSpecial(true);

    public SRecastEX()
    {
        super(DATA);
    }

    public SRecastEX(PSkill skill)
    {
        super(DATA, skill);
    }
}
