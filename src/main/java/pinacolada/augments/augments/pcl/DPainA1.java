package pinacolada.augments.augments.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PCLEnum;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.moves.PMove_LoseHP;

@VisibleAugment
public class DPainA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(DPainA1.class, 4, PCLAffinity.Purple)
            .setSkill(PCond.onOtherCardPlayed(), new PMove_LoseHP(1), PMove.dealDamageToAll(3, PCLEnum.AttackEffect.GHOST))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK, AbstractCard.CardType.SKILL));

    public DPainA1()
    {
        super(DATA);
    }

    public DPainA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
