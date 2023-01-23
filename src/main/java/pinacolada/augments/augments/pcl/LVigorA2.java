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
public class LVigorA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(LVigorA2.class, 3, PCLAffinity.Yellow)
            .setSkill(PMove.gain(9, PCLPowerHelper.Vigor), PTrait.hasCost(2))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public LVigorA2()
    {
        super(DATA);
    }

    public LVigorA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
