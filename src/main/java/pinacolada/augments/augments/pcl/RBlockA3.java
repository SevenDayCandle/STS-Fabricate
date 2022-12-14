package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class RBlockA3 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RBlockA3.class, 3, PCLAffinity.Red)
            .setSkill(PTrait.hasBlock(16), PTrait.hasCost(2))
            .setReqs(setTypes(AbstractCard.CardType.SKILL).setMaxRight(1));

    public RBlockA3()
    {
        super(DATA);
    }

    public RBlockA3(PSkill skill)
    {
        super(DATA, skill);
    }
}
