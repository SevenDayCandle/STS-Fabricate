package pinacolada.actions.creature;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.SmokeEffect;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.PCLCardAlly;

import java.util.ArrayList;
import java.util.Collection;

public class WithdrawAllyAction extends PCLActionWithCallback<ArrayList<PCLCard>>
{
    public final ArrayList<PCLCardAlly> allies = new ArrayList<>();
    public boolean trigger = true;
    public boolean showEffect = true;

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

    public WithdrawAllyAction setOptions(boolean trigger, boolean showEffect)
    {
        this.trigger = trigger;
        this.showEffect = showEffect;
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
                        ally.manualTrigger();
                    }
                    releaseCard(ally);
                }
            }
        }

        complete(returned);
    }

    protected void releaseCard(PCLCardAlly ally)
    {
        PCLCard returnedCard = ally.releaseCard();
        if (returnedCard != null)
        {
            PCLActions.top.makeCard(returnedCard, AbstractDungeon.player.discardPile)
                    .addCallback(returnedCard::unfadeOut);
        }

        // TODO better effects
        if (showEffect)
        {
            PCLEffects.Queue.add(new SmokeEffect(ally.hb.cX, ally.hb.cY));
        }

        CombatManager.onAllyWithdraw(returnedCard, ally);
    }
}
