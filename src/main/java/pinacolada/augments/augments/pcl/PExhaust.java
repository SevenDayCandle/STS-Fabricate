package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PExhaust extends PCLAugment {
    public static final PCLAugmentData DATA = register(PExhaust.class, PCLAugmentCategory.Played)
            .setMaxUpgrades(0)
            .setTier(3);

    public PExhaust(SaveData save) {
        super(DATA, save);
    }

    public void setup() {
        addUseMove(PTrait.tagsExact(1, PCLCardTag.Exhaust), PTrait.cost(-1));
    }
}
