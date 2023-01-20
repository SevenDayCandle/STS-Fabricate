package pinacolada.cards.pcl.status;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;

@VisibleCard
public class Status_Slimed extends PCLCard
{
    public static final PCLCardData DATA = register(Status_Slimed.class)
            .setStatus(1, CardRarity.COMMON, PCLCardTarget.None)
            .setTags(PCLCardTag.Exhaust);

    public Status_Slimed()
    {
        super(DATA);
    }
}