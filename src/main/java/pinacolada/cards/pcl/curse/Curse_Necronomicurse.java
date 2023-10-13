package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Curse_Necronomicurse extends PCLCard {
    public static final String ATLAS_URL = "curse/necronomicurse";
    public static final PCLCardData DATA = registerTemplate(Curse_Necronomicurse.class, Necronomicurse.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, true)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(2, PCLAffinity.Purple)
            .setRemovableFromDeck(false);

    public Curse_Necronomicurse() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
        addUseMove(PCond.onExhaust(), PMove.create(Curse_Necronomicurse.DATA.ID));
    }
}