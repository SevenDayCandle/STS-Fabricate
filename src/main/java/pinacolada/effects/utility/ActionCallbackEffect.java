package pinacolada.effects.utility;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import pinacolada.effects.PCLEffectWithCallback;

public class ActionCallbackEffect extends PCLEffectWithCallback<AbstractGameAction> {
    protected final AbstractGameAction action;
    protected float blackScreenAlpha;
    protected boolean updateIfScreenIsUp;

    public ActionCallbackEffect(AbstractGameAction action, ActionT0 onCompletion) {
        this(action);
        this.addCallback(onCompletion);
    }

    public ActionCallbackEffect(AbstractGameAction action) {
        super(1.0F);
        this.blackScreenAlpha = 0.0F;
        this.updateIfScreenIsUp = true;
        this.action = action;
    }

    public ActionCallbackEffect(AbstractGameAction action, ActionT1<AbstractGameAction> onCompletion) {
        this(action);
        this.addCallback(onCompletion);
    }

    public <S> ActionCallbackEffect(AbstractGameAction action, S state, ActionT2<S, AbstractGameAction> onCompletion) {
        this(action);
        this.addCallback(state, onCompletion);
    }

    public ActionCallbackEffect showBlackScreen(float alpha) {
        this.blackScreenAlpha = alpha;
        return this;
    }

    public void update() {
        if (!this.action.isDone) {
            if (this.updateIfScreenIsUp || !AbstractDungeon.isScreenUp) {
                this.action.update();
            }
        }
        else {
            this.complete(this.action);
        }

        if (this.blackScreenAlpha != 0.0F) {
            AbstractDungeon.overlayMenu.showBlackScreen(this.action.isDone ? 0.0F : this.blackScreenAlpha);
        }

    }

    public ActionCallbackEffect updateIfScreenIsUp(boolean value) {
        this.updateIfScreenIsUp = value;
        return this;
    }
}
