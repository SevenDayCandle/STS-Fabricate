package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.annotations.VisiblePower;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;

import static pinacolada.powers.PCLPowerData.DEFAULT_POWER_MAX;

@VisiblePower
public class EnergizedPower extends PCLPower {
    public static final PCLPowerData DATA = register(EnergizedPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.SingleTurnNext)
            .setLimits(-DEFAULT_POWER_MAX, DEFAULT_POWER_MAX)
            .setTooltip(PGR.core.tooltips.energized);

    public EnergizedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference) {
        this.type = amount < 0 ? PowerType.DEBUFF : PowerType.BUFF;
        super.onAmountChanged(previousAmount, difference);
    }

    @Override
    public void onEnergyRecharge() {
        if (this.amount < 0) {
            AbstractDungeon.player.loseEnergy(-this.amount);
        }
        else {
            AbstractDungeon.player.gainEnergy(this.amount);
        }
        removePower();
        this.flash();
    }
}
