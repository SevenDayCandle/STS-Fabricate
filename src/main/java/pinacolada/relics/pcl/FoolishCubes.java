package pinacolada.relics.pcl;

import pinacolada.annotations.VisibleRelic;
import pinacolada.relics.PCLRelicData;

@VisibleRelic
public class FoolishCubes extends AbstractCubes {
    public static final PCLRelicData DATA = register(FoolishCubes.class)
            .setProps(RelicTier.STARTER, LandingSound.SOLID);
    public static final int MAX_STORED_USES = 7;
    public static final int BONUS_PER_CARDS = 60;

    public FoolishCubes() {
        super(DATA, BONUS_PER_CARDS, MAX_STORED_USES);
    }
}