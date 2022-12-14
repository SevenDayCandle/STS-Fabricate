package pinacolada.actions.powers;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.misc.CombatStats;

public class AddPowerEffectBonus extends PCLActionWithCallback<AbstractPower>
{
    private final CombatStats.Type effectType;
    private String powerID;

    public AddPowerEffectBonus(String powerID, CombatStats.Type effectType, int amount)
    {
        super(ActionType.POWER, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.actionType = ActionType.POWER;
        this.effectType = effectType;
        this.powerID = powerID;

        initialize(amount);
    }

    public AddPowerEffectBonus(AbstractPower power, CombatStats.Type effectType, int amount)
    {
        super(ActionType.POWER, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        if (power != null)
        {
            this.powerID = power.ID;
        }
        this.effectType = effectType;

        initialize(amount);
    }

    @Override
    protected void firstUpdate()
    {
        if (powerID != null)
        {
            CombatStats.addBonus(powerID, effectType, amount);
            AbstractDungeon.onModifyPower();
        }
    }
}
