package pinacolada.cards.pcl.status;

import com.megacrit.cardcrawl.cards.status.Dazed;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

@VisibleCard(add = false)
public class Status_Dazed extends PCLCard {
    public static final String ATLAS_URL = "status/dazed";
    public static final PCLCardData DATA = registerTemplate(Status_Dazed.class, Dazed.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setStatus(-2, CardRarity.COMMON, PCLCardTarget.None)
            .setTags(PCLCardTag.Ethereal, PCLCardTag.Unplayable);

    public Status_Dazed() {
        super(DATA);
    }

}