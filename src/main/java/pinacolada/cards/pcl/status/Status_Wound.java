package pinacolada.cards.pcl.status;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

@VisibleCard
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