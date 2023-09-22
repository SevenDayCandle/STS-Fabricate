package pinacolada.actions.special;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.tags.PCLCardTag;
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
