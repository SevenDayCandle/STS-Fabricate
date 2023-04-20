package pinacolada.actions.creature;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.SmokeEffect;
import pinacolada.monsters.PCLCardAlly;

import java.util.ArrayList;
import java.util.Collection;

public class WithdrawAllyAction extends PCLAction<ArrayList<PCLCard>>
{
    public final ArrayList<PCLCardAlly> allies = new ArrayList<>();
    public boolean trigger = true;
    public boolean showEffect = true;
    public CardGroup destination = AbstractDungeon.player.discardPile;

    public WithdrawAllyAction(PCLCardAlly slot)
    {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, slot, 1);
        allies.add(slot);
    }

    public WithdrawAllyAction(Collection<PCLCardAlly> slot)
    {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, 1);
        allies.addAll(slot);
    }

    public WithdrawAllyAction setDestination(CardGroup destination)
    {
        this.destination = destination;
        return this;
    }

    public WithdrawAllyAction setOptions(boolean showEffect, boolean trigger)
    {
        this.showEffect = showEffect;
        this.trigger = trigger;
        return this;
    }

    @Override
    protected void firstUpdate()
    {
        ArrayList<PCLCard> returned = new ArrayList<>();

        for (PCLCardAlly ally : allies)
        {
            if (ally != null)
            {
                PCLCard returnedCard = ally.card;

                if (returnedCard != null)
                {
                    if (trigger)
                    {
                        ally.takeTurn();
                    }
                    releaseCard(ally);
                }

                returned.add(returnedCard);
            }
        }

        complete(returned);
    }

    protected void releaseCard(PCLCardAlly ally)
    {
        PCLCard returnedCard = ally.releaseCard();
        if (returnedCard != null)
        {
            PCLActions.top.makeCard(returnedCard, destination)
                    .addCallback(returnedCard::unfadeOut);
        }

        if (showEffect)
        {
            PCLEffects.Queue.add(new SmokeEffect(ally.hb.cX, ally.hb.cY));
        }

        CombatManager.onAllyWithdraw(returnedCard, ally);
    }
}
