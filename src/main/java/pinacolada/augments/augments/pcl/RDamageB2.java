package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class RDamageB2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RDamageB2.class, 2, PCLAffinity.Red)
            .setSkill(PTrait.hasDamageMultiplier(70), PTrait.hasCost(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK));

    public RDamageB2()
    {
        super(DATA);
    }

    public RDamageB2(PSkill skill)
    {
        super(DATA, skill);
    }
}
