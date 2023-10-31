package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.Injury;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

@VisibleCard(add = false)
public class Curse_Injury extends PCLCard {
    public static final String ATLAS_URL = "curse/injury";
    public static final PCLCardData DATA = registerTemplate(Curse_Injury.class, Injury.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple)
            .setLoadoutValue(-4);

    public Curse_Injury() {
        super(DATA);
    }
}