package pinacolada.actions.special;

import pinacolada.actions.PCLAction;
import pinacolada.dungeon.CombatManager;

public class AddOrRemoveSummonSlotAction extends PCLAction<Void> {
    public AddOrRemoveSummonSlotAction(int slotIncrease) {
        super(ActionType.SPECIAL);
        initialize(slotIncrease);
    }

    @Override
    protected void firstUpdate() {
        if (amount > 0) {
            CombatManager.summons.addSummon(amount);
        }
        else if (amount < 0) {
            CombatManager.summons.removeSummon(-amount);
        }
        complete(null);
    }
}
