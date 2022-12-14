package pinacolada.actions.utility;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import pinacolada.actions.PCLActionWithCallback;

public class CallbackAction extends PCLActionWithCallback<AbstractGameAction>
{
    private final AbstractGameAction action;

    public CallbackAction(AbstractGameAction action, ActionT0 onCompletion)
    {
        this(action);
        this.addCallback(onCompletion);
    }

    public CallbackAction(AbstractGameAction action, ActionT1<AbstractGameAction> onCompletion)
    {
        this(action);
        this.addCallback(onCompletion);
    }

    public <T> CallbackAction(AbstractGameAction action, T state, ActionT2<T, AbstractGameAction> onCompletion)
    {
        this(action);
        this.addCallback(state, onCompletion);
    }

    public CallbackAction(AbstractGameAction action)
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
