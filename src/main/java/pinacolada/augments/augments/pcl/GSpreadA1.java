package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class GSpreadA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(GSpreadA1.class, 2, PCLAffinity.Green)
            .setSkill(PTrait.hasCardTarget(PCLCardTarget.AllEnemy), PTrait.hasCost(1))
            .setReqs(setTargets(PCLCardTarget.Single, PCLCardTarget.RandomEnemy));

    public GSpreadA1()
    {
        super(DATA);
    }

    public GSpreadA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
