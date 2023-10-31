package pinacolada.cards.pcl.status;

import com.megacrit.cardcrawl.cards.status.Burn;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Status_Burn extends PCLCard {
    public static final String ATLAS_URL = "status/burn";
    public static final PCLCardData DATA = registerTemplate(Status_Burn.class, Burn.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setStatus(-2, CardRarity.COMMON, PCLCardTarget.Self)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Red)
            .setLoadoutValue(-7);

    public Status_Burn() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PCond.onTurnEnd(), PMove.takeDamage(2));
    }
}