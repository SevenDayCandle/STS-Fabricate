package pinacolada.skills.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnEndOfTurnFirstSubscriber;

public class DelayUseEndOfTurnFirst extends DelayUse implements OnEndOfTurnFirstSubscriber {
    public DelayUseEndOfTurnFirst(int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action, String title, String description) {
        super(turns, info, action, title, description);
    }

    @Override
    public DelayTiming getTiming() {
        return DelayTiming.EndOfTurnFirst;
    }

    @Override
    public void onEndOfTurnFirst(boolean b) {
        act();
    }
}
