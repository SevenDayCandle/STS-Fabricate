package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.annotations.VisiblePower;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;

@VisiblePower
public class SuperchargedPower extends PCLPower {
    public static final PCLPowerData DATA = register(SuperchargedPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.Permanent)
            .setTooltip(PGR.core.tooltips.supercharged);

    public SuperchargedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public void onEnergyRecharge() {
        AbstractDungeon.player.gainEnergy(this.amount);
        this.flash();
    }
}
