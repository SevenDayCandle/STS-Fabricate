package pinacolada.cards.pcl.status;

import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;

public class Status_Wound extends PCLCard
{
    public static final PCLCardData DATA = register(Status_Wound.class)
            .setStatus(-2, CardRarity.COMMON, PCLCardTarget.None)
            .setTags(PCLCardTag.Unplayable);

    public Status_Wound()
    {
        super(DATA);
    }
}