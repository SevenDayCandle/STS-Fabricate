package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLAttackType;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class GRangeA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(GRangeA1.class, 1, PCLAffinity.Green)
            .setSkill(PTrait.hasAttackType(PCLAttackType.Ranged))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK));

    public GRangeA1()
    {
        super(DATA);
    }

    public GRangeA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
