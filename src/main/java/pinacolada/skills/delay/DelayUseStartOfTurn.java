package pinacolada.skills.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnStartOfTurnSubscriber;

public class DelayUseStartOfTurn extends DelayUse implements OnStartOfTurnSubscriber
{
    public DelayUseStartOfTurn(int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        super(turns, info, action);
    }

    @Override
    public void onStartOfTurn()
    {
        act();
    }
}
