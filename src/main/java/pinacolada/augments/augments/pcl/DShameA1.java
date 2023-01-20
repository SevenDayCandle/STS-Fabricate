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
public class DShameA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(DShameA1.class, 2, PCLAffinity.Purple)
            .setSkill(PCond.onTurnEnd(), PMove.gain(1, PCLPowerHelper.Vulnerable), PMove.applyToEnemies(2, PCLPowerHelper.Vulnerable))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public DShameA1()
    {
        super(DATA);
    }

    public DShameA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
