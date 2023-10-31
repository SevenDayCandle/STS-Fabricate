package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

@VisibleCard(add = false)
public class Curse_CurseOfTheBell extends PCLCard {
    public static final String ATLAS_URL = "curse/curse_of_the_bell";
    public static final PCLCardData DATA = registerTemplate(Curse_CurseOfTheBell.class, CurseOfTheBell.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, true)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple)
            .setLoadoutValue(-9)
            .setRemovableFromDeck(false);

    public Curse_CurseOfTheBell() {
        super(DATA);
    }
}