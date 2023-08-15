package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pinacolada.actions.PCLActions;
import pinacolada.effects.PCLSFX;
import pinacolada.powers.PCLPower;
import pinacolada.resources.pcl.PCLCoreTooltips;

public class ShacklesPower extends PCLPower {
    public static final String POWER_ID = createFullID(ShacklesPower.class);

    public ShacklesPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        this.loadRegion(PCLCoreTooltips.ICON_SHACKLE);

        initialize(amount, PowerType.DEBUFF, false);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        removePower();
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference) {
        PCLActions.top.applyPower(new StrengthPower(owner, -difference));

        super.onAmountChanged(previousAmount, difference);
    }

    @Override
    public void playApplyPowerSfx() {
        PCLSFX.play(PCLSFX.POWER_SHACKLE, 0.95F, 1.05f);
    }
}