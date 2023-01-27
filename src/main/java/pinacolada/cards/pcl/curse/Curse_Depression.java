package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard
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