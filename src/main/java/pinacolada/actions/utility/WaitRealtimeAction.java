package pinacolada.actions.utility;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.actions.PCLAction;

public class WaitRealtimeAction extends PCLAction
{
    public WaitRealtimeAction(float duration)
    {
        super(AbstractGameAction.ActionType.WAIT, duration);

        this.isRealtime = true;

        initialize(1);
    }
}