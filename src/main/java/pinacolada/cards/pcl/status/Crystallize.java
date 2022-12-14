package pinacolada.cards.pcl.status;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.base.moves.PMove_LoseHP;

public class Crystallize extends PCLCard
{
    public static final PCLCardData DATA = register(Crystallize.class)
            .setStatus(1, CardRarity.UNCOMMON, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Blue)
            .setTags(PCLCardTag.Exhaust);

    public Crystallize()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(new PMove_LoseHP(4));
        addUseMove(PMove.gain(2, PCLPowerHelper.Metallicize));
    }
}