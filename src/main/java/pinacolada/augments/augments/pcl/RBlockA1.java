package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class RBlockA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RBlockA1.class, 1, PCLAffinity.Red)
            .setSkill(PTrait.hasBlock(1))
            .setReqs(setTypes(AbstractCard.CardType.SKILL));

    public RBlockA1()
    {
        super(DATA);
    }

    public RBlockA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
