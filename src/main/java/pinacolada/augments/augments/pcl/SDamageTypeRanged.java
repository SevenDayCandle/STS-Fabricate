package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.traits.PTrait_AttackType;

@VisibleAugment
public class SDamageTypeRanged extends PCLAugment {
    public static final PCLAugmentData DATA = register(SDamageTypeRanged.class, PCLAugmentCategorySub.DamageType, 1)
            .setSkill(new PTrait_AttackType(PCLAttackType.Ranged));

    public SDamageTypeRanged() {
        super(DATA);
    }

    public SDamageTypeRanged(PSkill<?> skill) {
        super(DATA, skill);
    }
}
