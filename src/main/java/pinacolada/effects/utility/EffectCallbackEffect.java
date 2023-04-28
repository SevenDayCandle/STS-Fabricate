package pinacolada.effects.utility;

import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import pinacolada.effects.PCLEffectWithCallback;

public class EffectCallbackEffect extends PCLEffectWithCallback<AbstractGameEffect> {
    private final AbstractGameEffect effect;

    public EffectCallbackEffect(AbstractGameEffect effect, ActionT0 onCompletion) {
        this(effect);
        this.addCallback(onCompletion);
    }

    public EffectCallbackEffect(AbstractGameEffect effect) {
        super(1.0F);
        this.effect = effect;
    }

    public EffectCallbackEffect(AbstractGameEffect effect, ActionT1<AbstractGameEffect> onCompletion) {
        this(effect);
        this.addCallback(onCompletion);
    }

    public <S> EffectCallbackEffect(AbstractGameEffect effect, S state, ActionT2<S, AbstractGameEffect> onCompletion) {
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

