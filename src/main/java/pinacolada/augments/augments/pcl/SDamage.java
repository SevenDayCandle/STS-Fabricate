package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.skills.PTrait;

@VisibleAugment
public class SDamage extends PCLAugment {
    public static final PCLAugmentData DATA = register(SDamage.class, PCLAugmentCategory.Summon)
            .setMaxUpgrades(3);

    public SDamage(SaveData save) {
        super(DATA, save);
    }

    public void setup() {
        addUseMove(PTrait.damage(1).setUpgrade(1));
    }
}
