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
public class GMagicA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(GMagicA1.class, 2, PCLAffinity.Green)
            .setSkill(PTrait.hasAttackType(PCLAttackType.Magical))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK));

    public GMagicA1()
    {
        super(DATA);
    }

    public GMagicA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
