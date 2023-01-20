package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.annotations.VisibleRelic;

@VisibleRelic
public class FoolishCubes extends AbstractCubes
{
    public static final String ID = createFullID(FoolishCubes.class);
    public static final int MAX_STORED_USES = 5;
    public static final int USES_PER_ELITE = 1;
    public static final int USES_PER_NORMAL = 1;
    private static final CardGroup tempGroup1 = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
    private static final CardGroup tempGroup2 = new CardGroup(CardGroup.CardGroupType.CARD_POOL);

    public FoolishCubes()
    {
        super(ID, RelicTier.STARTER, LandingSound.SOLID, USES_PER_NORMAL, USES_PER_ELITE, MAX_STORED_USES);
    }
}