package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.powers.PCLPower;

public class VitalityPower extends PCLPower {
    public static final String POWER_ID = createFullID(VitalityPower.class);

    public VitalityPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        initialize(amount);
    }

    @Override
    public void atStartOfTurn() {
        super.atStartOfTurn();

        PCLActions.bottom.gainTemporaryHP(amount);
    }
}
