package pinacolada.actions.creature;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.cards.base.PCLCard;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.PCLCardAlly;

public class TriggerAllyAction extends PCLActionWithCallback<PCLCardAlly>
{
    public final PCLCardAlly ally;

    public TriggerAllyAction(PCLCardAlly slot)
    {
        this(slot, 1);
    }

    public TriggerAllyAction(PCLCardAlly slot, int amount)
    {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, slot, amount);
        this.ally = slot;
    }

    @Override
    protected void firstUpdate()
    {
        if (this.ally == null || !this.ally.hasCard())
        {
            complete();
            return;
        }

        // Record card first in case the ally's actions cause it to lose the card
        PCLCard card = ally.card;
        for (int i = 0; i < amount; i++)
        {
            ally.takeTurn();
            CombatManager.onAllyTrigger(card, ally);
        }
        complete(ally);
    }
}
