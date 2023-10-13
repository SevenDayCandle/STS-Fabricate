package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.Shame;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Curse_Shame extends PCLCard {
    public static final String ATLAS_URL = "curse/shame";
    public static final PCLCardData DATA = registerTemplate(Curse_Shame.class, Shame.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Shame() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
        addUseMove(PCond.onTurnEnd(), PMove.gain(1, PCLPowerData.Frail));
    }
}