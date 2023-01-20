package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class RDamageB1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RDamageB1.class, 1, PCLAffinity.Red)
            .setSkill(PTrait.hasDamageMultiplier(15))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK));

    public RDamageB1()
    {
        super(DATA);
    }

    public RDamageB1(PSkill skill)
    {
        super(DATA, skill);
    }
}
