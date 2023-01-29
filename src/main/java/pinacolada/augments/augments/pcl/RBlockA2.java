package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class RBlockA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RBlockA2.class, 2, PCLAffinity.Red)
            .setSkill(PTrait.block(8), PTrait.cost(1))
            .setReqs(setTypes(AbstractCard.CardType.SKILL).setMaxRight(1));

    public RBlockA2()
    {
        super(DATA);
    }

    public RBlockA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
