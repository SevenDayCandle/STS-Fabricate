package pinacolada.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.utilities.GenericCallback;

import java.util.ArrayList;

public abstract class PCLActionWithCallback<T> extends PCLAction
{
    protected ArrayList<GenericCallback<T>> callbacks = new ArrayList<>();

    public PCLActionWithCallback(AbstractGameAction.ActionType type)
    {
        super(type);
    }

    public PCLActionWithCallback(AbstractGameAction.ActionType type, float duration)
    {
        super(type, duration);
    }

    public <S> PCLActionWithCallback<T> addCallback(S state, ActionT2<S, T> onCompletion)
    {
        callbacks.add(GenericCallback.fromT2(onCompletion, state));

        return this;
    }

    public PCLActionWithCallback<T> addCallback(ActionT1<T> onCompletion)
    {
        callbacks.add(GenericCallback.fromT1(onCompletion));

        return this;
    }

    public PCLActionWithCallback<T> addCallback(ActionT0 onCompletion)
    {
        callbacks.add(GenericCallback.fromT0(onCompletion));

        return this;
    }

    protected void complete(T result)
    {
        for (GenericCallback<T> callback : callbacks)
        {
            callback.complete(result);
        }

        complete();
    }

    @Override
    protected void Import(PCLAction other)
    {
        super.Import(other);

        callbacks.addAll(((PCLActionWithCallback<T>) other).callbacks);
    }
}
