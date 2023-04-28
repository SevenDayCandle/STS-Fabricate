package pinacolada.skills.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnStartOfTurnPostDrawSubscriber;

public class DelayUseStartOfTurnPostDraw extends DelayUse implements OnStartOfTurnPostDrawSubscriber
{
    public DelayUseStartOfTurnPostDraw(int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        super(turns, info, action);
    }

    @Override
    public void onStartOfTurnPostDraw()
    {
        act();
    }
}
