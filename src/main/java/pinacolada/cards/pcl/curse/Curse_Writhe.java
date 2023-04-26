package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.base.moves.PMove_Cycle;

@VisibleCard
public class Curse_Writhe extends PCLCard {
    public static final String ATLAS_URL = "curse/writhe";
    public static final PCLCardData DATA = register(Curse_Writhe.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Unplayable.make(), PCLCardTag.Innate.make(-1))
            .setAffinities(PCLAffinity.Purple);

    public Curse_Writhe() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
        addUseMove(PCond.onExhaust(), new PMove_Cycle(1));
    }
}