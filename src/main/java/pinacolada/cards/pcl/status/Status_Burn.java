package pinacolada.cards.pcl.status;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

public class Status_Burn extends PCLCard
{
    public static final PCLCardData DATA = register(Status_Burn.class)
            .setStatus(-2, CardRarity.COMMON, PCLCardTarget.AllEnemy, true)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Red);

    public Status_Burn()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addUseMove(PCond.onTurnEnd(), PMove.gain(2, PCLPowerHelper.Blasted));
        addUseMove(PCond.onExhaust(), PMove.applyToRandom(2, PCLPowerHelper.Blasted));
    }
}