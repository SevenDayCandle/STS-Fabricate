package pinacolada.augments.augments.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class SBlockEX extends PCLAugment
{

    public static final PCLAugmentData DATA = register(SBlockEX.class, 5, PCLAffinity.Silver)
            .setSkill(PTrait.hasBlockMultiplier(100), PTrait.hasCost(1))
            .setReqs(setTypes(AbstractCard.CardType.SKILL))
            .setSpecial(true);

    public SBlockEX()
    {
        super(DATA);
    }

    public SBlockEX(PSkill skill)
    {
        super(DATA, skill);
    }
}
