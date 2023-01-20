package pinacolada.cards.pcl.replacement;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PMove;

@VisibleCard
public class Miracle extends PCLCard
{
    public static final PCLCardData DATA = register(Miracle.class)
            .setSkill(0, CardRarity.SPECIAL, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Yellow)
            .setTags(PCLCardTag.Purge.make(), PCLCardTag.Retain.make(-1))
            .setColorless();

    public Miracle()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addUseMove(PMove.gainEnergy(1).setUpgrade(1));
    }
}