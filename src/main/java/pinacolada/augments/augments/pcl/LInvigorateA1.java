package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class LInvigorateA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(LInvigorateA1.class, 3, PCLAffinity.Yellow)
            .setSkill(PMove.gain(2, PCLPowerHelper.Invigorated), PTrait.hasCost(2))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public LInvigorateA1()
    {
        super(DATA);
    }

    public LInvigorateA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
