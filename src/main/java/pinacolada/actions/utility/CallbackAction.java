package pinacolada.actions.utility;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import pinacolada.actions.PCLAction;

// Copied and modified from STS-AnimatorMod
// Variant of nestedAction that returns the action being called and that can accept arbitrary actions
public class CallbackAction<T extends AbstractGameAction> extends PCLAction<T>
{
    private final T action;

    public CallbackAction(T action, ActionT0 onCompletion)
    {
        this(action);
        this.addCallback(onCompletion);
    }

    public CallbackAction(T action, ActionT1<T> onCompletion)
    {
        this(action);
        this.addCallback(onCompletion);
    }

    public <U> CallbackAction(T action, U state, ActionT2<U, T> onCompletion)
    {
        this(action);
        this.addCallback(state, onCompletion);
    }

    public CallbackAction(T action)
    {
        super(action.actionType);
        this.action = action;
        this.initialize(action.source, action.target, action.amount);
    }

    public void update()
    {
        if (this.updateAction())
        {
            this.complete(this.action);
        }
    }

    private boolean updateAction()
    {
        if (!this.action.isDone)
        {
            this.action.update();
        }

        return this.action.isDone;
    }
}
