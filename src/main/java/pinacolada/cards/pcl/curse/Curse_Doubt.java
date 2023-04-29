package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.Doubt;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard
public class Curse_Doubt extends PCLCard {
    public static final String ATLAS_URL = "curse/doubt";
    public static final PCLCardData DATA = registerTemplate(Curse_Doubt.class, Doubt.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Doubt() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
        addUseMove(PCond.onTurnEnd(), PMove.gain(1, PCLPowerHelper.Weak));
    }
}