package pinacolada.relics.pcl;

import pinacolada.annotations.VisibleRelic;
import pinacolada.relics.PCLPointerRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.skills.skills.PShift;
import pinacolada.skills.skills.base.moves.PMove_ObtainCard;

@VisibleRelic
public class UsefulBox extends PCLPointerRelic {
    public static final PCLRelicData DATA = registerTemplate(UsefulBox.class)
            .setTier(RelicTier.SPECIAL);

    public UsefulBox() {
        super(DATA);
    }

    public void setup() {
        addUseMove(PShift.obtain(), new PMove_ObtainCard(3, 6));
    }
}