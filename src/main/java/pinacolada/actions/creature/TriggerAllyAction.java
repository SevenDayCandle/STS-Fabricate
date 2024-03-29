package pinacolada.actions.creature;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLAction;
import pinacolada.monsters.PCLCardAlly;

public class TriggerAllyAction extends PCLAction<PCLCardAlly> {
    public final PCLCardAlly ally;
    public boolean manual = true;

    public TriggerAllyAction(PCLCardAlly slot) {
        this(slot, 1);
    }

    public TriggerAllyAction(PCLCardAlly slot, int amount) {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, slot, amount);
        this.ally = slot;
    }

    @Override
    protected void firstUpdate() {
        if (this.ally == null || !this.ally.hasCard()) {
            complete(null);
            return;
        }

        for (int i = 0; i < amount; i++) {
            ally.takeTurn(manual);
        }
        complete(ally);
    }

    public TriggerAllyAction setManual(boolean value) {
        this.manual = value;
        return this;
    }
}
