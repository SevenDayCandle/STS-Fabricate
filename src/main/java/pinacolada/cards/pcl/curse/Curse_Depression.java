package pinacolada.cards.pcl.curse;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

public class Curse_Depression extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_Depression.class)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Ethereal, PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Depression()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onDraw(), PMove.discardRandom(1));
        addUseMove(PCond.onPurge(), PMove.draw(1));
    }
}