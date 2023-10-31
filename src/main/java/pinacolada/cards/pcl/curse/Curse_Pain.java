package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.Pain;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.base.moves.PMove_LoseHP;

@VisibleCard(add = false)
public class Curse_Pain extends PCLCard {
    public static final String ATLAS_URL = "curse/pain";
    public static final PCLCardData DATA = registerTemplate(Curse_Pain.class, Pain.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple)
            .setLoadoutValue(-8);

    public Curse_Pain() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
        addUseMove(PCond.onOtherCardPlayed(), new PMove_LoseHP(1));
    }
}