package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

@VisibleAugment
public class GAimA1 extends PCLAugment
{

    public static final PCLAugmentData DATA = register(GAimA1.class, 1, PCLAffinity.Green)
            .setSkill(PTrait.hasCardTarget(PCLCardTarget.Single))
            .setReqs(setTargets(PCLCardTarget.RandomEnemy, PCLCardTarget.Self));

    public GAimA1()
    {
        super(DATA);
    }

    public GAimA1(PSkill skill)
    {
        super(DATA, skill);
    }
}
