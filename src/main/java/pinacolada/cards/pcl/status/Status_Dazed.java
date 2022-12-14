package pinacolada.cards.pcl.status;

import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;

public class Status_Dazed extends PCLCard
{
    public static final PCLCardData DATA = register(Status_Dazed.class)
            .setStatus(-2, CardRarity.COMMON, PCLCardTarget.None)
            .setTags(PCLCardTag.Ethereal, PCLCardTag.Unplayable);

    public Status_Dazed()
    {
        super(DATA);
    }

}