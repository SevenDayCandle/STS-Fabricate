package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;

public class DRegretA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(DRegretA1.class, 3, PCLAffinity.Dark)
            .setSkill(PCond.onDraw(), PMove.gain(3, PCLPowerHelper.DelayedDamage), PMove.dealDamageToRandom(9))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public DRegretA1()
    {
        super(DATA);
    }

    public DRegretA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
