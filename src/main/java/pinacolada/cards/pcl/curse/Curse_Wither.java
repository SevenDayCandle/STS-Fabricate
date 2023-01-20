package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard
public class Curse_Wither extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_Wither.class)
            .setCurse(-2, PCLCardTarget.None, false, true)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Wither()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onTurnEnd(), PMove.gain(1, PCLPowerHelper.Vulnerable));
    }
}