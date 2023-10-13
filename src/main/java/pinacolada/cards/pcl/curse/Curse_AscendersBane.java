package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

@VisibleCard(add = false)
public class Curse_AscendersBane extends PCLCard {
    public static final String ATLAS_URL = "curse/ascenders_bane";
    public static final PCLCardData DATA = registerTemplate(Curse_AscendersBane.class, AscendersBane.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, true)
            .setTags(PCLCardTag.Ethereal, PCLCardTag.Unplayable)
            .setRemovableFromDeck(false);

    public Curse_AscendersBane() {
        super(DATA);
    }
}