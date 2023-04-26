package pinacolada.effects.utility;

import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import pinacolada.effects.PCLEffectWithCallback;

public class CallbackEffect2 extends PCLEffectWithCallback<AbstractGameEffect> {
    private final AbstractGameEffect effect;

    public CallbackEffect2(AbstractGameEffect effect, ActionT0 onCompletion) {
        this(effect);
        this.addCallback(onCompletion);
    }

    public CallbackEffect2(AbstractGameEffect effect) {
        super(1.0F);
        this.effect = effect;
    }

    public CallbackEffect2(AbstractGameEffect effect, ActionT1<AbstractGameEffect> onCompletion) {
        this(effect);
        this.addCallback(onCompletion);
    }

    public <S> CallbackEffect2(AbstractGameEffect effect, S state, ActionT2<S, AbstractGameEffect> onCompletion) {
        this(effect);
        this.addCallback(state, onCompletion);
    }

    public void update() {
        if (!this.effect.isDone) {
            this.effect.update();
        }
        else {
            this.complete(this.effect);
        }

    }
}

