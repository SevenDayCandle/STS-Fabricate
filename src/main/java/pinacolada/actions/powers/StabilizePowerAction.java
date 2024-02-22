package pinacolada.actions.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.PCLAction;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.StabilizingPower;
import pinacolada.utilities.GameUtilities;

public class StabilizePowerAction extends PCLAction<AbstractPower> {
    private AbstractPower sourcePower;
    public final String powerID;
    public boolean showEffect = true;
    public boolean isFast = true;

    public StabilizePowerAction(AbstractCreature source, AbstractCreature target, PCLPowerData ph, int amount) {
        this(source, target, ph.ID, amount);
    }

    public StabilizePowerAction(AbstractCreature source, AbstractCreature target, String powerID, int amount) {
        super(ActionType.POWER, Settings.ACTION_DUR_XFAST);
        initialize(source, target, amount);
        this.powerID = powerID;
    }

    @Override
    protected void firstUpdate() {
        if (sourcePower == null) {
            sourcePower = GameUtilities.getPower(target, powerID);
        }

        if (sourcePower != null) {
            StabilizingPower spower = new StabilizingPower(sourcePower, target, source, amount);
            GameUtilities.applyPowerInstantly(target, spower);
            spower.subscribeToAll();
        }

        complete(sourcePower);
    }

    public StabilizePowerAction setPower(AbstractPower po) {
        this.sourcePower = po;
        return this;
    }

    public StabilizePowerAction showEffect(boolean showEffect, boolean isFast) {
        this.showEffect = showEffect;
        this.isFast = isFast;

        return this;
    }

}
