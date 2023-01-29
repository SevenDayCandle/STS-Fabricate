package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class RDamageA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RDamageA2.class, 2, PCLAffinity.Red)
            .setSkill(PTrait.damage(9), PTrait.cost(1))
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
