package pinacolada.relics.pcl;

import pinacolada.annotations.VisibleRelic;
import pinacolada.relics.PCLRelicData;

@VisibleRelic
public class SpitefulCubes extends AbstractCubes {
    public static final PCLRelicData DATA = register(SpitefulCubes.class)
            .setTier(RelicTier.STARTER, LandingSound.SOLID);
    public static final int MAX_STORED_USES = 4;
    public static final int BONUS_PER_CARDS = 40;


    public SpitefulCubes() {
        super(DATA, BONUS_PER_CARDS, MAX_STORED_USES);
    }
}