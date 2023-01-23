package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard
public class Curse_SearingBurn extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_SearingBurn.class)
            .setCurse(-2, PCLCardTarget.AllEnemy, false, false)
            .setTags(PCLCardTag.Ethereal, PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Red);

    public Curse_SearingBurn()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onExhaust(),PMove.apply(PCLCardTarget.All, 2, PCLPowerHelper.Blasted));
    }
}