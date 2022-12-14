package pinacolada.powers.special;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.powers.PCLPower;

public class SuperchargedPower extends PCLPower
{
    public static final String POWER_ID = createFullID(SuperchargedPower.class);

    public SuperchargedPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        initialize(amount);
    }

    @Override
    public void onEnergyRecharge()
    {
        AbstractDungeon.player.gainEnergy(this.amount);
        this.flash();
    }
}
