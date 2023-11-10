package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PTrait;

@VisibleAugment
public class SHP extends PCLAugment {
    public static final PCLAugmentData DATA = register(SHP.class, PCLAugmentCategory.Summon)
            .setMaxUpgrades(2);

    public SHP(SaveData save) {
        super(DATA, save);
    }

    public void setup() {
        addUseMove(PTrait.hp(2).setUpgrade(6), PTrait.cost(0).setUpgrade(1));
    }
}
