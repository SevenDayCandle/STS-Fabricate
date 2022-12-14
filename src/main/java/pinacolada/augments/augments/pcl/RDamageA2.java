package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class RDamageA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RDamageA2.class, 2, PCLAffinity.Red)
            .setSkill(PTrait.hasDamage(9), PTrait.hasCost(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK).setMaxHits(1));

    public RDamageA2()
    {
        super(DATA);
    }

    public RDamageA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
