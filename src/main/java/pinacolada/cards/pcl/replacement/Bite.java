package pinacolada.cards.pcl.replacement;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.effects.AttackEffects;
import pinacolada.skills.PMove;

@VisibleCard
public class Bite extends PCLCard
{
    public static final PCLCardData DATA = register(Bite.class)
            .setAttack(1, CardRarity.SPECIAL, PCLAttackType.Normal, PCLCardTarget.Single)
            .setDamage(6, 3)
            .setAffinities(PCLAffinity.Purple)
            .setColorless();

    public Bite()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addDamageMove(AttackEffects.BITE);
        addUseMove(PMove.gainTempHP(4));
    }
}