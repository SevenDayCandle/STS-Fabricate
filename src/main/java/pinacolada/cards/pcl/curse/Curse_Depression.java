package pinacolada.cards.pcl.curse;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.base.moves.PMove_Discard;
import pinacolada.skills.skills.base.moves.PMove_Draw;

public class Curse_Depression extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_Depression.class)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Ethereal, PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Dark);

    public Curse_Depression()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onDraw(), new PMove_Discard(1).setAlt(true));
        addUseMove(PCond.onPurge(), new PMove_Draw(1));
    }
}