package pinacolada.skills.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.misc.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnEndOfTurnFirstSubscriber;

public class DelayUseEndOfTurnFirst extends DelayUse implements OnEndOfTurnFirstSubscriber
{
    public DelayUseEndOfTurnFirst(int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action)
    {
        super(turns, info, action);
    }

    @Override
    public void onEndOfTurnFirst(boolean b)
    {
        act();
    }
}