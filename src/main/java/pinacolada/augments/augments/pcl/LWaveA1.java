package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class LWaveA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(LWaveA1.class, 2, PCLAffinity.Yellow)
            .setSkill(PMove.gain(2, PCLPowerHelper.SupportDamage), PTrait.hasCost(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public LWaveA1()
    {
        super(DATA);
    }

    public LWaveA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
