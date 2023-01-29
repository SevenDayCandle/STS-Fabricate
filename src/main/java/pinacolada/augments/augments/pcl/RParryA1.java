package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class RParryA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(RParryA1.class, 1, PCLAffinity.Red)
            .setSkill(PTrait.blockCount(1))
            .setReqs(setTypes(AbstractCard.CardType.SKILL).setMaxBlock(3));

    public RParryA1()
    {
        super(DATA);
    }

    public RParryA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
