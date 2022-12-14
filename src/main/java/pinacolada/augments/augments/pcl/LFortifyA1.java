package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class LFortifyA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(LFortifyA1.class, 3, PCLAffinity.Light)
            .setSkill(PMove.gain(2, PCLPowerHelper.Fortified), PTrait.hasCost(2))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public LFortifyA1()
    {
        super(DATA);
    }

    public LFortifyA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
