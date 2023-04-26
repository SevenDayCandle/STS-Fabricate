package pinacolada.relics.pcl;

import pinacolada.annotations.VisibleRelic;

@VisibleRelic
public class SpitefulCubes extends AbstractCubes {
    public static final String ID = createFullID(SpitefulCubes.class);
    public static final int MAX_STORED_USES = 4;
    public static final int BONUS_PER_CARDS = 40;


    public SpitefulCubes() {
        super(ID, RelicTier.STARTER, LandingSound.SOLID, BONUS_PER_CARDS, MAX_STORED_USES);
    }
}