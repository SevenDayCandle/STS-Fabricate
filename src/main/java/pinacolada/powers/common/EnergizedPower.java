package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.powers.PCLPower;

public class EnergizedPower extends PCLPower {
    public static final String POWER_ID = createFullID(EnergizedPower.class);

    public EnergizedPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        initialize(amount);
    }

    @Override
    public void onEnergyRecharge() {
        AbstractDungeon.player.gainEnergy(this.amount);
        removePower();
        this.flash();
    }
}
