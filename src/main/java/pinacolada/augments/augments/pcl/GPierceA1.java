package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class GPierceA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(GPierceA1.class, 2, PCLAffinity.Green)
            .setSkill(PTrait.hasAttackType(PCLAttackType.Piercing), PTrait.hasCost(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK));

    public GPierceA1()
    {
        super(DATA);
    }

    public GPierceA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
