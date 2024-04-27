package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.utilities.CostFilter;
import pinacolada.annotations.VisibleRelic;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.relics.PCLPointerRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.PShift;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.moves.PMove_ObtainCard;
import pinacolada.skills.skills.special.primary.PRoot;

@VisibleRelic
public class UsefulBox extends PCLPointerRelic {
    public static final PCLRelicData DATA = registerTemplate(UsefulBox.class)
            .setTier(RelicTier.SPECIAL);

    public UsefulBox() {
        super(DATA);
    }

    public void setup() {
        addUseMove(PShift.obtain(), new PMove_ObtainCard(1, 3));
        addUseMove(new PRoot(), PMove.createRandom(1, 3, PCLCardGroupHelper.Hand).edit(f -> f.setCost(CostFilter.Cost0)));
    }
}