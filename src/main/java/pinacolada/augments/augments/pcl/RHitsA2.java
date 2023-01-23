package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class RHitsA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RHitsA2.class, 2, PCLAffinity.Red)
            .setSkill(PTrait.hasHits(2), PTrait.hasCost(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK).setMaxDamage(3));

    public RHitsA2()
    {
        super(DATA);
    }

    public RHitsA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
