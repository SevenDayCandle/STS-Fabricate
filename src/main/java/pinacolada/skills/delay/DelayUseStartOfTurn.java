package pinacolada.skills.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnStartOfTurnSubscriber;

public class DelayUseStartOfTurn extends DelayUse implements OnStartOfTurnSubscriber {
    public DelayUseStartOfTurn(int turns, PCLUseInfo info, ActionT1<PCLUseInfo> action, String title, String description) {
        super(turns, info, action, title, description);
    }

    @Override
    public DelayTiming getTiming() {
        return DelayTiming.StartOfTurnFirst;
    }

    @Override
    public void onStartOfTurn() {
        act();
    }
}
