package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class LVenomA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(LVenomA2.class, 3, PCLAffinity.Yellow)
            .setSkill(PMove.applyToSingle(9, PCLPowerHelper.Poison), PTrait.hasCost(2))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public LVenomA2()
    {
        super(DATA);
    }

    public LVenomA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
