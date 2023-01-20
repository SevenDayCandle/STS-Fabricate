package pinacolada.actions.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.PCLActions;
import pinacolada.misc.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class ReducePower extends PCLActionWithCallback<AbstractPower>
{
    private String powerID;
    private AbstractPower power;
    private boolean isDebuffInteraction;

    public ReducePower(AbstractCreature target, AbstractCreature source, String powerID, int amount)
    {
        super(ActionType.REDUCE_POWER, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.powerID = powerID;

        initialize(target, source, amount);
    }

    public ReducePower(AbstractCreature target, AbstractCreature source, AbstractPower power, int amount)
    {
        super(ActionType.REDUCE_POWER, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.power = power;

        initialize(target, source, amount);
    }

    @Override
    protected void firstUpdate()
    {
        if (shouldCancelAction() || !GameUtilities.canReducePower(source, target, power, this))
        {
            complete(power);
            return;
        }

        if (this.powerID != null)
        {
            power = this.target.getPower(this.powerID);
        }

        if (power != null)
        {
            final int initialAmount = power.amount;
            if (power.canGoNegative && power.amount < 0)
            {
                power.stackPower(amount);
            }
            else
            {
                power.reducePower(amount);
            }

            power.updateDescription();
            AbstractDungeon.onModifyPower();
            if (isDebuffInteraction)
            {
                CombatManager.onModifyDebuff(power, initialAmount, power.amount);
            }

            final PCLPower p = EUIUtils.safeCast(power, PCLPower.class);
            if (power.amount == 0 && (p == null || !p.canBeZero))
            {
                PCLActions.top.removePower(source, target, power);
            }
        }
    }

    public ReducePower isDebuffInteraction(boolean value)
    {
        isDebuffInteraction = value;

        return this;
    }
}
