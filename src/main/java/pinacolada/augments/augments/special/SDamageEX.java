package pinacolada.augments.augments.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

public class SDamageEX extends PCLAugment
{

    public static final PCLAugmentData DATA = register(SDamageEX.class, 5, PCLAffinity.Silver)
            .setSkill(PTrait.hasDamageMultiplier(100), PTrait.hasCost(1))
            .setReqs(setTypes(AbstractCard.CardType.ATTACK))
            .setSpecial(true);

    public SDamageEX()
    {
        super(DATA);
    }

    public SDamageEX(PSkill skill)
    {
        super(DATA, skill);
    }
}
