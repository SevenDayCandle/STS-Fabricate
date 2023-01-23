package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;


@VisibleAugment
public class DClumsyA1 extends PCLAugment
{
    public static final PCLAugmentData DATA = register(DClumsyA1.class, 2, PCLAffinity.Purple)
            .setSkill(PCond.onTurnEnd(), PMove.applyToEveryone(2, PCLPowerHelper.Blinded))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public DClumsyA1()
    {
        super(DATA);
    }

    public DClumsyA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
