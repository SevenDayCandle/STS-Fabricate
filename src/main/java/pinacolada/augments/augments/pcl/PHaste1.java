package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrait;

@VisibleAugment
public class PHaste1 extends PCLAugment {
    public static final PCLAugmentData DATA = register(PHaste1.class, PCLAugmentCategorySub.TagHaste, 1)
            .setSkill(PTrait.tags(PCLCardTag.Haste));

    public PHaste1() {
        super(DATA);
    }

    public PHaste1(PSkill<?> skill) {
        super(DATA, skill);
    }
}
