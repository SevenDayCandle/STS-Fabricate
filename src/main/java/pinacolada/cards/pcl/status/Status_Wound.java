package pinacolada.cards.pcl.status;

import com.megacrit.cardcrawl.cards.status.Wound;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

@VisibleCard(add = false)
public class Status_Wound extends PCLCard {
    public static final String ATLAS_URL = "status/wound";
    public static final PCLCardData DATA = registerTemplate(Status_Wound.class, Wound.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setStatus(-2, CardRarity.COMMON, PCLCardTarget.None)
            .setTags(PCLCardTag.Unplayable);

    public Status_Wound() {
        super(DATA);
    }
}