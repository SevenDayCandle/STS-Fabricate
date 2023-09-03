package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.annotations.VisiblePower;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;

@VisiblePower
public class EnergizedPower extends PCLPower {
    public static final PCLPowerData DATA = register(EnergizedPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.SingleTurnNext)
            .setTooltip(PGR.core.tooltips.energized);

    public EnergizedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public void onEnergyRecharge() {
        AbstractDungeon.player.gainEnergy(this.amount);
        removePower();
        this.flash();
    }
}
