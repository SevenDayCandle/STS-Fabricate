package pinacolada.actions;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public abstract class PCLAction<T> extends AbstractGameAction {
    protected final AbstractPlayer player;
    protected AbstractCard card;
    protected ArrayList<ActionT1<T>> callbacks = new ArrayList<>();
    protected String message;
    protected String name;
    protected int ticks;
    public PCLActions.ActionOrder originalOrder;
    public boolean canCancel;
    public boolean isRealtime;

    public PCLAction(ActionType type) {
        this(type, Settings.ACTION_DUR_FAST);
    }

    public PCLAction(ActionType type, float duration) {
        this.player = AbstractDungeon.player;
        this.duration = this.startDuration = duration;
        this.actionType = type;
        this.canCancel = true;
    }

    public PCLAction<T> addCallback(ActionT0 onCompletion) {
        callbacks.add((__) -> onCompletion.invoke());

        return this;
    }

    public PCLAction<T> addCallback(ActionT1<T> onCompletion) {
        callbacks.add(onCompletion);

        return this;
    }

    public <S> PCLAction<T> addCallback(S item, ActionT1<S> onCompletion) {
        callbacks.add((__) -> onCompletion.invoke(item));

        return this;
    }

    public <S> PCLAction<T> addCallback(S item, ActionT2<S, T> onCompletion) {
        callbacks.add((result) -> onCompletion.invoke(item, result));

        return this;
    }

    public PCLAction<T> addDuration(float duration) {
        this.startDuration += duration;
        this.duration += duration;

        return this;
    }

    protected void complete(T result) {
        for (ActionT1<T> callback : callbacks) {
            callback.invoke(result);
        }

        completeImpl();
    }

    protected void completeImpl() {
        this.isDone = true;
    }

    protected void copySettings(PCLAction<T> other) {
        setDuration(other.startDuration, other.isRealtime);
        isCancellable(other.canCancel);
        name = other.name;
        message = other.message;
        originalOrder = other.originalOrder;
        callbacks.addAll(other.callbacks);
    }

    protected void firstUpdate() {

    }

    protected float getDeltaTime() {
        return isRealtime ? Gdx.graphics.getRawDeltaTime() : Gdx.graphics.getDeltaTime();
    }

    protected void initialize(int amount) {
        initialize(player, amount, null);
    }

    protected void initialize(AbstractCreature target, int amount, String name) {
        initialize(target, target, amount, name);
    }

    protected void initialize(AbstractCreature source, AbstractCreature target, int amount, String name) {
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.name = name;
    }

    protected void initialize(int amount, String name) {
        initialize(player, amount, name);
    }

    protected void initialize(AbstractCreature target, int amount) {
        initialize(target, target, amount, null);
    }

    protected void initialize(AbstractCreature source, AbstractCreature target, int amount) {
        initialize(source, target, amount, null);
    }

    // Set this to false if an action needs to be executed even if all enemies are dead (e.g. Gain Gold or Heal)
    public PCLAction<T> isCancellable(boolean canCancel) {
        this.canCancel = canCancel;

        return this;
    }

    public PCLAction<T> setDuration(float duration, boolean isRealtime) {
        this.isRealtime = isRealtime;
        this.duration = this.startDuration = duration;

        return this;
    }

    public PCLAction<T> setOriginalOrder(PCLActions.ActionOrder order) {
        this.originalOrder = order;

        return this;
    }

    public PCLAction<T> setRealtime(boolean isRealtime) {
        this.isRealtime = isRealtime;

        return this;
    }

    protected boolean tickDuration(float deltaTime) {
        this.ticks += 1;
        this.duration -= deltaTime;

        if (this.duration < 0f && ticks >= 3) // ticks are necessary for SuperFastMode at 1000% speed
        {
            this.isDone = true;
        }

        return isDone;
    }

    @Override
    protected final void tickDuration() {
        tickDuration(getDeltaTime());
    }

    @Override
    public void update() {
        if (duration == startDuration) {
            firstUpdate();

            if (!this.isDone) {
                tickDuration(getDeltaTime());
            }
        }
        else {
            updateInternal(getDeltaTime());
        }
    }

    protected void updateInternal(float deltaTime) {
        if (tickDuration(deltaTime)) {
            completeImpl();
        }
    }

    public String updateMessage() {
        return updateMessageInternal(message);
    }

    protected String updateMessageInternal(String message) {
        return EUIUtils.format(message, amount) + (StringUtils.isNotEmpty(name) ? (" [" + name + "]") : EUIUtils.EMPTY_STRING);
    }
}