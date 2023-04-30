package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.Writhe;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

@VisibleCard
public class Curse_Writhe extends PCLCard {
    public static final String ATLAS_URL = "curse/writhe";
    public static final PCLCardData DATA = registerTemplate(Curse_Writhe.class, Writhe.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Unplayable.make(), PCLCardTag.Innate.make(-1))
            .setAffinities(PCLAffinity.Purple);

    public Curse_Writhe() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
    }
}