package pinacolada.blights.pcl;

import basemod.BaseMod;
import pinacolada.annotations.VisibleBlight;
import pinacolada.blights.PCLBlight;
import pinacolada.blights.PCLBlightData;

@VisibleBlight
public class UpgradedHand extends PCLBlight {
    public static final PCLBlightData DATA = register(UpgradedHand.class)
            .setUnique(true);

    public UpgradedHand() {
        this(1);
    }

    public UpgradedHand(int turns) {
        super(DATA);
        this.counter = turns;
    }

    public void addAmount(int amount) {
        this.counter += amount;
        BaseMod.MAX_HAND_SIZE += amount;
    }

    @Override
    public void onEquip() {
        BaseMod.MAX_HAND_SIZE += this.counter;
    }

    public void reset() {
        BaseMod.MAX_HAND_SIZE -= this.counter;
        this.counter = 0;
    }
}