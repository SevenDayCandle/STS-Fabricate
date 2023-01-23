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
public class LCrushA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(LCrushA1.class, 2, PCLAffinity.Yellow)
            .setSkill(PMove.applyToSingle(2, PCLPowerHelper.Vulnerable), PTrait.hasCost(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public LCrushA1()
    {
        super(DATA);
    }

    public LCrushA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
