package pinacolada.cards.pcl.status;

import com.megacrit.cardcrawl.cards.status.VoidCard;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.base.moves.PMove_GainEnergy;

@VisibleCard(add = false)
public class Status_Void extends PCLCard {
    public static final String ATLAS_URL = "status/void";
    public static final PCLCardData DATA = registerTemplate(Status_Void.class, VoidCard.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setStatus(-2, CardRarity.COMMON, PCLCardTarget.None)
            .setTags(PCLCardTag.Ethereal, PCLCardTag.Unplayable)
            .setAffinities(2, PCLAffinity.Purple)
            .setLoadoutValue(-8);

    public Status_Void() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
        addUseMove(PCond.onDraw(), new PMove_GainEnergy(-1));
    }
}