package pinacolada.augments.augments.pcl;

import pinacolada.annotations.VisibleAugment;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PTrait;

@VisibleAugment
public class PHaste extends PCLAugment {
    public static final PCLAugmentData DATA = register(PHaste.class, PCLAugmentCategory.Played)
            .setMaxUpgrades(1);

    public PHaste(SaveData save) {
        super(DATA, save);
    }

    public void setup() {
        addUseMove(PTrait.tags(1, PCLCardTag.Haste).setUpgrade(3), PTrait.tags(0, PCLCardTag.Ethereal).setUpgrade(1));
    }
}
