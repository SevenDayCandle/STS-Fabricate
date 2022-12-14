package pinacolada.actions.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.PCLActions;

public class IncreasePower extends PCLActionWithCallback<AbstractPower>
{
    private String powerID;
    private AbstractPower power;

    public IncreasePower(AbstractCreature target, AbstractCreature source, String powerID, int amount)
    {
        super(ActionType.REDUCE_POWER, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.actionType = ActionType.REDUCE_POWER;

        initialize(target, source, amount);
    }

    public IncreasePower(AbstractCreature target, AbstractCreature source, AbstractPower power, int amount)
    {
        super(ActionType.REDUCE_POWER, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.power = power;

        initialize(target, source, amount);
    }

    @Override
    protected void firstUpdate()
    {
        if (this.powerID != null)
        {
            power = this.target.getPower(this.powerID);
        }

        if (power != null)
        {
            power.stackPower(amount);
            power.updateDescription();
            AbstractDungeon.onModifyPower();

            if (power.amount == 0)
            {
                PCLActions.top.removePower(source, target, power);
            }
        }
    }
}
