package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.powers.PCLPower;
import pinacolada.actions.PCLActions;

public class DeenergizedPower extends PCLPower
{
    public static final String POWER_ID = createFullID(DeenergizedPower.class);

    public DeenergizedPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        this.amount = amount;
        this.type = PowerType.DEBUFF;

        updateDescription();
    }

    public void onEnergyRecharge()
    {
        if (owner.isPlayer)
        {
            PCLActions.bottom.spendEnergy(amount, true);
            flash();
            PCLActions.bottom.removePower(owner, owner, this);
        }
    }
}