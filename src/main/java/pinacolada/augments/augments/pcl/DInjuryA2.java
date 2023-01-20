package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;

@VisibleAugment
public class DInjuryA2 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(DInjuryA2.class, 4, PCLAffinity.Purple)
            .setSkill(PCond.onTurnEnd(), PMove.takeDamage(6), PMove.dealDamageToAll(16))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public DInjuryA2()
    {
        super(DATA);
    }

    public DInjuryA2(PSkill skill)
    {
        super(DATA, skill);
    }
}
