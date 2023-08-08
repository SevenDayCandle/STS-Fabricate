package pinacolada.skills.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnEndOfTurnLastSubscriber;

public class DelayUseEndOfTurnLast extends DelayUse implements OnEndOfTurnLastSubscriber {
    public DelayUseEndOfTurnLast(int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action, String title, String description) {
        super(turns, info, action, title, description);
    }

    @Override
    public DelayTiming getTiming() {
        return DelayTiming.EndOfTurnLast;
    }

    @Override
    public void onEndOfTurnLast(boolean b) {
        act();
    }
}
