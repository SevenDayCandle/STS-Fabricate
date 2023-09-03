package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisiblePower;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;

@VisiblePower
public class VitalityPower extends PCLPower {
    public static final PCLPowerData DATA = register(VitalityPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.Permanent)
            .setTooltip(PGR.core.tooltips.vitality);

    public VitalityPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public void atStartOfTurn() {
        super.atStartOfTurn();

        PCLActions.bottom.gainTemporaryHP(amount);
    }
}
