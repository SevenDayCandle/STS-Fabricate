package pinacolada.effects;

import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public abstract class PCLEffectWithCallback<T> extends PCLEffect {
    protected ArrayList<ActionT1<T>> callbacks = new ArrayList<>();

    public PCLEffectWithCallback() {
        super();
    }

    public PCLEffectWithCallback(float duration) {
        super(duration);
    }

    public PCLEffectWithCallback(float duration, boolean isRealtime) {
        super(duration, isRealtime);
    }

    public PCLEffectWithCallback<T> addCallback(ActionT1<T> onCompletion) {
        callbacks.add(onCompletion);

        return this;
    }

    public PCLEffectWithCallback<T> addCallback(ActionT0 onCompletion) {
        callbacks.add((__) -> onCompletion.invoke());

        return this;
    }

    public <S> PCLEffectWithCallback<T> addCallback(S item, ActionT1<S> onCompletion) {
        callbacks.add((__) -> onCompletion.invoke(item));

        return this;
    }

    public <S> PCLEffectWithCallback<T> addCallback(S item, ActionT2<S, T> onCompletion) {
        callbacks.add((result) -> onCompletion.invoke(item, result));

        return this;
    }


    protected void complete(T result) {
        for (ActionT1<T> callback : callbacks) {
            callback.invoke(result);
        }

        complete();
    }
}
