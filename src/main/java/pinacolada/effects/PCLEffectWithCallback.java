package pinacolada.effects;

import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.utilities.GenericCallback;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public abstract class PCLEffectWithCallback<T> extends PCLEffect {
    protected ArrayList<GenericCallback<T>> callbacks = new ArrayList<>();

    public PCLEffectWithCallback() {
        super();
    }

    public PCLEffectWithCallback(float duration) {
        super(duration);
    }

    public PCLEffectWithCallback(float duration, boolean isRealtime) {
        super(duration, isRealtime);
    }

    public <S> PCLEffectWithCallback<T> addCallback(S state, ActionT2<S, T> onCompletion) {
        callbacks.add(GenericCallback.fromT2(onCompletion, state));

        return this;
    }

    public PCLEffectWithCallback<T> addCallback(ActionT1<T> onCompletion) {
        callbacks.add(GenericCallback.fromT1(onCompletion));

        return this;
    }

    public PCLEffectWithCallback<T> addCallback(ActionT0 onCompletion) {
        callbacks.add(GenericCallback.fromT0(onCompletion));

        return this;
    }

    protected void complete(T result) {
        for (GenericCallback<T> callback : callbacks) {
            callback.complete(result);
        }

        complete();
    }
}
