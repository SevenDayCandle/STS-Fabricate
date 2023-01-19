package pinacolada.actions;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;

// Copied and modified from STS-AnimatorMod
public abstract class PCLAction extends AbstractGameAction
{
    public static AbstractCard currentCard;

    public boolean canCancel;
    public boolean isRealtime;
    public PCLActions.ActionOrder originalOrder;
    public AbstractCard sourceCard;

    protected final AbstractPlayer player;
    protected final Random rng;
    protected AbstractCard card;
    protected String message;
    protected String name;
    protected int ticks;

    public PCLAction(ActionType type)
    {
        this(type, Settings.ACTION_DUR_FAST);
    }

    public PCLAction(ActionType type, float duration)
    {
        this.rng = AbstractDungeon.cardRandomRng;
        this.player = AbstractDungeon.player;
        this.sourceCard = currentCard;
        this.duration = this.startDuration = duration;
        this.actionType = type;
        this.canCancel = true;
    }

    public PCLAction setSourceCard(AbstractCard card)
    {
        this.sourceCard = card;

        return this;
    }

    public PCLAction setOriginalOrder(PCLActions.ActionOrder order)
    {
        this.originalOrder = order;

        return this;
    }

    // Set this to false if an action needs to be executed even if all enemies are dead (e.g. Gain Gold or Heal)
    public PCLAction isCancellable(boolean canCancel)
    {
        this.canCancel = canCancel;

        return this;
    }

    public PCLAction setRealtime(boolean isRealtime)
    {
        this.isRealtime = isRealtime;

        return this;
    }

    public PCLAction setDuration(float duration, boolean isRealtime)
    {
        this.isRealtime = isRealtime;
        this.duration = this.startDuration = duration;

        return this;
    }

    public PCLAction addDuration(float duration)
    {
        this.startDuration += duration;
        this.duration += duration;

        return this;
    }

    protected void initialize(int amount)
    {
        initialize(player, amount, null);
    }

    protected void initialize(int amount, String name)
    {
        initialize(player, amount, name);
    }

    protected void initialize(AbstractCreature target, int amount)
    {
        initialize(target, target, amount, null);
    }

    protected void initialize(AbstractCreature target, int amount, String name)
    {
        initialize(target, target, amount, name);
    }

    protected void initialize(AbstractCreature source, AbstractCreature target, int amount)
    {
        initialize(source, target, amount, null);
    }

    protected void initialize(AbstractCreature source, AbstractCreature target, int amount, String name)
    {
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.name = name;
    }

    protected String updateMessageInternal(String message)
    {
        return EUIUtils.format(message, amount) + (StringUtils.isNotEmpty(name) ? (" [" + name + "]") : "");
    }

    public String updateMessage()
    {
        return updateMessageInternal(message);
    }

    @Override
    public void update()
    {
        if (duration == startDuration)
        {
            firstUpdate();

            if (!this.isDone)
            {
                tickDuration(getDeltaTime());
            }
        }
        else
        {
            updateInternal(getDeltaTime());
        }
    }

    protected void firstUpdate()
    {

    }

    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            complete();
        }
    }

    protected void complete()
    {
        this.isDone = true;
    }

    protected float getDeltaTime()
    {
        return isRealtime ? Gdx.graphics.getRawDeltaTime() : Gdx.graphics.getDeltaTime();
    }

    protected boolean tickDuration(float deltaTime)
    {
        this.ticks += 1;
        this.duration -= deltaTime;

        if (this.duration < 0f && ticks >= 3) // ticks are necessary for SuperFastMode at 1000% speed
        {
            this.isDone = true;
        }

        return isDone;
    }

    @Override
    protected final void tickDuration()
    {
        tickDuration(getDeltaTime());
    }

    protected void copySettings(PCLAction other)
    {
        setDuration(other.startDuration, other.isRealtime);
        isCancellable(other.canCancel);
        name = other.name;
        message = other.message;
        originalOrder = other.originalOrder;
        sourceCard = other.sourceCard;
    }
}