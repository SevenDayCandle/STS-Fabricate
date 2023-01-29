package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class LVenomA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(LVenomA1.class, 2, PCLAffinity.Yellow)
            .setSkill(PMove.applyToSingle(4, PCLPowerHelper.Poison), PTrait.cost(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public LVenomA1()
    {
        super(DATA);
    }

    public LVenomA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
