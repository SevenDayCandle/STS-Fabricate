package pinacolada.cards.pcl.status;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard
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
        addUseMove(PCond.onTurnEnd(), PMove.takeDamage(2));
    }
}