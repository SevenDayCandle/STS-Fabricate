package pinacolada.cards.pcl.replacement;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;

@VisibleCard
public class JAX extends PCLCard
{
    public static final PCLCardData DATA = register(JAX.class)
            .setSkill(0, CardRarity.SPECIAL, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Purple)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public JAX()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addUseMove(PMove.gain(6, PCLPowerHelper.DelayedDamage));
        addUseMove(PMove.gain(4, PCLPowerHelper.Vigor, PCLPowerHelper.Sorcery).setUpgrade(1));
    }
}