package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class RBlockB1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RBlockB1.class, 1, PCLAffinity.Red)
            .setSkill(PTrait.hasBlockMultiplier(15))
            .setReqs(setTypes(AbstractCard.CardType.SKILL));

    public RBlockB1()
    {
        super(DATA);
    }

    public RBlockB1(PSkill skill)
    {
        super(DATA, skill);
    }
}
