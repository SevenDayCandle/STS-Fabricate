package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard
public class Curse_Parasite extends PCLCard {
    public static final String ATLAS_URL = "curse/parasite";
    public static final PCLCardData DATA = register(Curse_Parasite.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Green, PCLAffinity.Purple);

    public Curse_Parasite() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
        addUseMove(PCond.onExhaust(), PMove.loseHp(2));
    }
}