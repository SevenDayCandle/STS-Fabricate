package pinacolada.cards.pcl.status;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

@VisibleCard
public class Status_Slimed extends PCLCard {
    public static final String ATLAS_URL = "status/slimed";
    public static final PCLCardData DATA = register(Status_Slimed.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setStatus(1, CardRarity.COMMON, PCLCardTarget.None)
            .setTags(PCLCardTag.Exhaust);

    public Status_Slimed() {
        super(DATA);
    }
}