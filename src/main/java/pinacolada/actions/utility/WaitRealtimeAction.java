package pinacolada.actions.utility;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.actions.PCLAction;

// Copied and modified from STS-AnimatorMod
public class WaitRealtimeAction extends PCLAction<Void>
{
    public WaitRealtimeAction(float duration)
    {
        super(AbstractGameAction.ActionType.WAIT, duration);

        this.isRealtime = true;

        initialize(1);
    }
}