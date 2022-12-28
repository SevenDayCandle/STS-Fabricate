package pinacolada.cards.pcl.status;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.base.moves.PMove_GainEnergy;

public class Status_Void extends PCLCard
{
    public static final PCLCardData DATA = register(Status_Void.class)
            .setStatus(-2, CardRarity.COMMON, PCLCardTarget.None)
            .setTags(PCLCardTag.Ethereal, PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Status_Void()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onDraw(), new PMove_GainEnergy(-1));
    }
}