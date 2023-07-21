package pinacolada.actions.powers;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.PCLAction;
import pinacolada.dungeon.CombatManager;

public class AddPowerEffectBonus extends PCLAction<AbstractPower> {
    private final boolean forPlayer;
    private final String powerID;

    public AddPowerEffectBonus(String powerID, int amount, boolean effectType) {
        super(ActionType.POWER, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.actionType = ActionType.POWER;
        this.forPlayer = effectType;
        this.powerID = powerID;

        initialize(amount);
    }

    @Override
    protected void firstUpdate() {
        if (powerID != null) {
            CombatManager.addBonus(powerID, amount, forPlayer);
            AbstractDungeon.onModifyPower();
        }
    }
}
