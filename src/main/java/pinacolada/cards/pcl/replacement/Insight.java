package pinacolada.cards.pcl.replacement;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PMove;

public class Insight extends PCLCard
{
    public static final PCLCardData DATA = register(Insight.class)
            .setSkill(0, CardRarity.SPECIAL, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Light)
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