package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLAttackType;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class GNormalizeA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(GNormalizeA1.class, 1, PCLAffinity.Green)
            .setSkill(PTrait.hasAttackType(PCLAttackType.Normal))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK));

    public GNormalizeA1()
    {
        super(DATA);
    }

    public GNormalizeA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
