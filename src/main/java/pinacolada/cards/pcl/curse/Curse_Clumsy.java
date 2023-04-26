package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

@VisibleCard
public class Curse_Clumsy extends PCLCard {
    public static final String ATLAS_URL = "curse/clumsy";
    public static final PCLCardData DATA = register(Curse_Clumsy.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Unplayable, PCLCardTag.Ethereal)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Clumsy() {
        super(DATA);
    }
}