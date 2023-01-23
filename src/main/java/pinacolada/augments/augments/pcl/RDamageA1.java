package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class RDamageA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RDamageA1.class, 1, PCLAffinity.Red)
            .setSkill(PTrait.hasDamage(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK));

    public RDamageA1()
    {
        super(DATA);
    }

    public RDamageA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
