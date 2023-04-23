package pinacolada.skills.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.subscribers.*;
import pinacolada.skills.skills.DelayTiming;
import pinacolada.utilities.GameUtilities;

public class DelayUse implements PCLCombatSubscriber, OnStartOfTurnPostDrawSubscriber, OnStartOfTurnSubscriber, OnEndOfTurnLastSubscriber, OnEndOfTurnFirstSubscriber
{
    protected int baseTurns;
    protected int turns;
    protected final DelayTiming timing;
    protected final ActionT1<PCLUseInfo> onUse;
    protected PCLUseInfo capturedCardUseInfo;

    public static DelayUse turnEnd(PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        return turnEnd(0, info, action);
    }

    public static DelayUse turnEndLast(PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        return turnEndLast(0, info, action);
    }

    public static DelayUse turnEnd(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        return new DelayUse(DelayTiming.EndOfTurnFirst, amount, info, action);
    }

    public static DelayUse turnEndLast(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        return new DelayUse(DelayTiming.EndOfTurnLast, amount, info, action);
    }

    public static DelayUse turnStart(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        return new DelayUse(DelayTiming.StartOfTurnFirst, amount, info, action);
    }

    public static DelayUse turnStartLast(int amount, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        return new DelayUse(DelayTiming.StartOfTurnLast, amount, info, action);
    }

    public DelayUse(DelayTiming timing, int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        this.timing = timing;
        baseTurns = this.turns = turns;
        capturedCardUseInfo = info;
        onUse = action;
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
                capturedCardUseInfo = CombatManager.playerSystem.generateInfo(capturedCardUseInfo.card, capturedCardUseInfo.source, GameUtilities.getRandomEnemy(true));
            }
            onUse.invoke(capturedCardUseInfo);
            CombatManager.unsubscribe(this);
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
                CombatManager.subscribe(OnEndOfTurnLastSubscriber.class, this);
                return;
            case EndOfTurnLast:
                CombatManager.subscribe(OnEndOfTurnFirstSubscriber.class, this);
                return;
            case StartOfTurnFirst:
                CombatManager.subscribe(OnStartOfTurnSubscriber.class, this);
                return;
            case StartOfTurnLast:
                CombatManager.subscribe(OnStartOfTurnPostDrawSubscriber.class, this);
        }
    }
}
