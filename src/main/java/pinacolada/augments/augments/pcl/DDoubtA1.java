package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;

public class DDoubtA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(DDoubtA1.class, 2, PCLAffinity.Purple)
            .setSkill(PCond.onTurnEnd(), PMove.applyToEveryone(2, PCLPowerHelper.Weak))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public DDoubtA1()
    {
        super(DATA);
    }

    public DDoubtA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
