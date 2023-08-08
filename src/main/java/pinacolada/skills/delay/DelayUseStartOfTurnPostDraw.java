package pinacolada.skills.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnStartOfTurnPostDrawSubscriber;

public class DelayUseStartOfTurnPostDraw extends DelayUse implements OnStartOfTurnPostDrawSubscriber {
    public DelayUseStartOfTurnPostDraw(int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action, String title, String description) {
        super(turns, info, action, title, description);
    }

    @Override
    public DelayTiming getTiming() {
        return DelayTiming.StartOfTurnLast;
    }

    @Override
    public void onStartOfTurnPostDraw() {
        act();
    }
}
