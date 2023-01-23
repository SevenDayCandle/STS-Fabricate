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
public class LSpikesA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(LSpikesA2.class, 3, PCLAffinity.Yellow)
            .setSkill(PMove.gainTemporary(8, PCLPowerHelper.Thorns), PTrait.hasCost(2))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public LSpikesA2()
    {
        super(DATA);
    }

    public LSpikesA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
