package pinacolada.cards.pcl.status;

import com.megacrit.cardcrawl.cards.status.Slimed;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

@VisibleCard(add = false)
public class Status_Slimed extends PCLCard {
    public static final String ATLAS_URL = "status/slimed";
    public static final PCLCardData DATA = registerTemplate(Status_Slimed.class, Slimed.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setStatus(1, CardRarity.COMMON, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Green)
            .setTags(PCLCardTag.Exhaust);

    public Status_Slimed() {
        super(DATA);
    }
}