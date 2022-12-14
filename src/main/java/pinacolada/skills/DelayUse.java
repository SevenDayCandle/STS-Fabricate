package pinacolada.skills;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.subscribers.OnEndOfTurnFirstSubscriber;
import pinacolada.interfaces.subscribers.OnEndOfTurnLastSubscriber;
import pinacolada.interfaces.subscribers.OnStartOfTurnPostDrawSubscriber;
import pinacolada.interfaces.subscribers.OnStartOfTurnSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.utilities.GameUtilities;

public class DelayUse implements OnStartOfTurnPostDrawSubscriber, OnStartOfTurnSubscriber, OnEndOfTurnFirstSubscriber, OnEndOfTurnLastSubscriber
{
    protected int baseTurns;
    protected int turns;
    protected PCLUseInfo capturedCardUseInfo;
    protected ActionT1<PCLUseInfo> onUse;
    protected Timing timing = Timing.StartOfTurnLast;

    public static DelayUse turnEnd(AbstractCreature target, ActionT1<PCLUseInfo> action)
    {
        return turnEnd(0, target, action);
    }

    public static DelayUse turnEndLast(AbstractCreature target, ActionT1<PCLUseInfo> action)
    {
        return turnEndLast(0, target, action);
    }

    public static DelayUse turnEnd(int amount, AbstractCreature target, ActionT1<PCLUseInfo> action)
    {
        return new DelayUse(amount, DelayUse.Timing.EndOfTurnFirst, null, action);
    }

    public static DelayUse turnEndLast(int amount, AbstractCreature target, ActionT1<PCLUseInfo> action)
    {
        return new DelayUse(amount, DelayUse.Timing.EndOfTurnLast, null, action);
    }

    public static DelayUse turnStart(int amount, AbstractCreature target, ActionT1<PCLUseInfo> action)
    {
        return new DelayUse(amount, DelayUse.Timing.StartOfTurnFirst, null, action);
    }

    public static DelayUse turnStartLast(int amount, AbstractCreature target, ActionT1<PCLUseInfo> action)
    {
        return new DelayUse(amount, DelayUse.Timing.StartOfTurnLast, null, action);
    }

    public DelayUse(int turns, Timing timing, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        baseTurns = this.turns = turns;
        this.timing = timing;
        capturedCardUseInfo = info;
        onUse = action;
    }

    public static void schedule(int turns, Timing timing, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        DelayUse move = new DelayUse(turns, timing, info, action);
        move.start();
    }

    protected void act()
    {
        turns -= 1;
        if (turns <= 0)
        {
            if (capturedCardUseInfo.card != null)
            {
                PCLEffects.Queue.showCardBriefly(capturedCardUseInfo.card.makeStatEquivalentCopy());
            }
            if (capturedCardUseInfo.target == null || GameUtilities.isDeadOrEscaped(capturedCardUseInfo.target))
            {
                capturedCardUseInfo = new PCLUseInfo(capturedCardUseInfo.card, capturedCardUseInfo.source, GameUtilities.getRandomEnemy(true));
            }
            onUse.invoke(capturedCardUseInfo);
            CombatManager.onEndOfTurnFirst.unsubscribe(this);
            CombatManager.onEndOfTurnLast.unsubscribe(this);
            CombatManager.onStartOfTurn.unsubscribe(this);
            CombatManager.onStartOfTurnPostDraw.unsubscribe(this);
        }
    }

    @Override
    public void onEndOfTurnFirst(boolean b)
    {
        act();
    }

    @Override
    public void onEndOfTurnLast(boolean b)
    {
        act();
    }

    @Override
    public void onStartOfTurn()
    {
        act();
    }

    @Override
    public void onStartOfTurnPostDraw()
    {
        act();
    }

    public void start()
    {
        turns = baseTurns;
        switch (timing)
        {
            case EndOfTurnFirst:
                CombatManager.onEndOfTurnFirst.subscribe(this);
                break;
            case EndOfTurnLast:
                CombatManager.onEndOfTurnLast.subscribe(this);
                break;
            case StartOfTurnFirst:
                CombatManager.onStartOfTurn.subscribe(this);
                break;
            default:
                CombatManager.onStartOfTurnPostDraw.subscribe(this);
        }
    }

    public enum Timing
    {
        EndOfTurnFirst,
        EndOfTurnLast,
        StartOfTurnFirst,
        StartOfTurnLast
    }


}
