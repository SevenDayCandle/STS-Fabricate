package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class RHitsA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RHitsA1.class, 1, PCLAffinity.Red)
            .setSkill(PTrait.hitCount(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK).setMaxDamage(3));

    public RHitsA1()
    {
        super(DATA);
    }

    public RHitsA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
