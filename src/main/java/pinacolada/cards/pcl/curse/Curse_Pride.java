package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.Pride;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard
public class Curse_Pride extends PCLCard {
    public static final String ATLAS_URL = "curse/pride";
    public static final PCLCardData DATA = registerTemplate(Curse_Pride.class, Pride.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(1, PCLCardTarget.None, true)
            .setTags(PCLCardTag.Innate, PCLCardTag.Exhaust)
            .setAffinities(PCLAffinity.Star);

    public Curse_Pride() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
        addUseMove(PCond.onTurnEnd(), PMove.createDrawPile(1).edit(f -> f.setDestination(PCLCardSelection.Top).setForced(true)));
    }
}