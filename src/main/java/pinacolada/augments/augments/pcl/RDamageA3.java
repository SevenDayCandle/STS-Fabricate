package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class RDamageA3 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RDamageA3.class, 3, PCLAffinity.Red)
            .setSkill(PTrait.hasDamage(18), PTrait.hasCost(2))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK).setMaxHits(1));

    public RDamageA3()
    {
        super(DATA);
    }

    public RDamageA3(PSkill skill)
    {
        super(DATA, skill);
    }
}
