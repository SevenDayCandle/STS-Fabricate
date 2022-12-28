package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;

public class DInjuryA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(DInjuryA1.class, 2, PCLAffinity.Purple)
            .setSkill(PCond.onTurnEnd(), PMove.takeDamage(2), PMove.dealDamageToAll(4))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public DInjuryA1()
    {
        super(DATA);
    }

    public DInjuryA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
