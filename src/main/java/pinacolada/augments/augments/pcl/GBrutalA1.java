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
public class GBrutalA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(GBrutalA1.class, 3, PCLAffinity.Green)
            .setSkill(PTrait.hasAttackType(PCLAttackType.Brutal))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK));

    public GBrutalA1()
    {
        super(DATA);
    }

    public GBrutalA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
