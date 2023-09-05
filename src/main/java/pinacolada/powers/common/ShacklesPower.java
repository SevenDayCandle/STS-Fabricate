package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisiblePower;
import pinacolada.effects.PCLSFX;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreTooltips;

@VisiblePower
public class ShacklesPower extends PCLPower {
    public static final PCLPowerData DATA = register(ShacklesPower.class)
            .setType(PowerType.DEBUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.SingleTurn)
            .setIsCommon(true)
            .setImageRegion(PCLPowerData.ICON_SHACKLE)
            .setTooltip(PGR.core.tooltips.shackles);

    public ShacklesPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
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