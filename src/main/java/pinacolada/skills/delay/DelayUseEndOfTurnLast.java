package pinacolada.skills.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnEndOfTurnLastSubscriber;

public class DelayUseEndOfTurnLast extends DelayUse implements OnEndOfTurnLastSubscriber
{
    public DelayUseEndOfTurnLast(int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        super(turns, info, action);
    }

    @Override
    public void onEndOfTurnLast(boolean b)
    {
        act();
    }
}
