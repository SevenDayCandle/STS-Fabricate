package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.powers.PCLPower;

public class WardingPower extends PCLPower {
    public static final String POWER_ID = createFullID(WardingPower.class);

    public WardingPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        initialize(amount);
    }

    @Override
    public float modifyBlock(float block) {
        return block + amount;
    }

    public void onGainedBlock(float amount) {
        super.onGainedBlock(amount);
        if (amount > 0) {
            removePower();
        }
    }
}
