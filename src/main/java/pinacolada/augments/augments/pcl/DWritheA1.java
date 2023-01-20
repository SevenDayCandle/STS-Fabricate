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
public class DWritheA1 extends PCLAugment
{
    public static final PCLAugmentData DATA = register(DWritheA1.class, 3, PCLAffinity.Purple)
            .setSkill(PCond.onDraw(), PMove.cycle(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public DWritheA1()
    {
        super(DATA);
    }

    public DWritheA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
