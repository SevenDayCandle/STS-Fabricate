package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.Normality;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PTrait;
import pinacolada.skills.skills.PTrigger;

@VisibleCard(add = false)
public class Curse_Normality extends PCLCard {
    public static final String ATLAS_URL = "curse/normality";
    public static final PCLCardData DATA = registerTemplate(Curse_Normality.class, Normality.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Unplayable)
            .setLoadoutValue(-9);

    public Curse_Normality() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
        addUseMove(PTrigger.passive(PCond.havePlayed(3), PTrait.unplayable()).edit(f -> f.setCardGroup(PCLCardGroupHelper.Hand)));
    }
}