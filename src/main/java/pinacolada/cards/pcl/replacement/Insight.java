package pinacolada.cards.pcl.replacement;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;

@VisibleCard
public class Insight extends PCLCard
{
    public static final PCLCardData DATA = register(Insight.class)
            .setSkill(0, CardRarity.SPECIAL, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Yellow)
            .setTags(PCLCardTag.Purge.make(), PCLCardTag.Retain.make(-1))
            .setColorless();

    public Insight()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addUseMove(PMove.draw(2).setUpgrade(1));
    }
}