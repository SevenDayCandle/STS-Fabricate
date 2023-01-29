package pinacolada.relics.pcl;

import pinacolada.annotations.VisibleRelic;

@VisibleRelic
public class FoolishCubes extends AbstractCubes
{
    public static final String ID = createFullID(FoolishCubes.class);
    public static final int MAX_STORED_USES = 7;
    public static final int BONUS_PER_CARDS = 60;

    public FoolishCubes()
    {
        super(ID, RelicTier.STARTER, LandingSound.SOLID, BONUS_PER_CARDS, MAX_STORED_USES);
    }
}