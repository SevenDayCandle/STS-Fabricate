package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;

@VisibleAugment
public class DDecayA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(DDecayA1.class, 3, PCLAffinity.Purple)
            .setSkill(PCond.onTurnEnd(), PMove.gain(2, PCLPowerHelper.Poison), PMove.applyToEnemies(4, PCLPowerHelper.Poison))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public DDecayA1()
    {
        super(DATA);
    }

    public DDecayA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
