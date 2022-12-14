package pinacolada.ui.common;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.utilities.GenericCallback;

public class ControllableCard
{
    public final AbstractCard card;
    public ControllableCardPile sourcePile;
    protected boolean enabled;
    protected FuncT1<Boolean, ControllableCard> useCondition;
    protected GenericCallback<ControllableCard> onUpdate;
    protected GenericCallback<ControllableCard> onSelect;
    protected GenericCallback<ControllableCard> onDelete;

    public ControllableCard(ControllableCardPile sourcePile, AbstractCard card)
    {
        this.sourcePile = sourcePile;
        this.card = card;
        this.enabled = true;
    }

    public boolean canUse()
    {
        return enabled && AbstractDungeon.getCurrMapNode() != null && card != null && (useCondition == null || useCondition.invoke(this));
    }

    public void delete()
    {
        sourcePile.remove(this);

        if (onDelete != null)
        {
            onDelete.complete(this);
        }
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public <S> ControllableCard onDelete(S state, ActionT2<S, ControllableCard> onCompletion)
    {
        onDelete = GenericCallback.fromT2(onCompletion, state);

        return this;
    }

    public ControllableCard onDelete(ActionT1<ControllableCard> onCompletion)
    {
        onDelete = GenericCallback.fromT1(onCompletion);

        return this;
    }

    public <S> ControllableCard onSelect(S state, ActionT2<S, ControllableCard> onCompletion)
    {
        onSelect = GenericCallback.fromT2(onCompletion, state);

        return this;
    }

    public ControllableCard onSelect(ActionT1<ControllableCard> onCompletion)
    {
        onSelect = GenericCallback.fromT1(onCompletion);

        return this;
    }

    public <S> ControllableCard onUpdate(S state, ActionT2<S, ControllableCard> onCompletion)
    {
        onUpdate = GenericCallback.fromT2(onCompletion, state);

        return this;
    }

    public ControllableCard onUpdate(ActionT1<ControllableCard> onCompletion)
    {
        onUpdate = GenericCallback.fromT1(onCompletion);

        return this;
    }

    public void render(SpriteBatch sb)
    {
        card.render(sb);
    }

    public void select()
    {
        if (onSelect != null)
        {
            onSelect.complete(this);
        }
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public ControllableCard setUseCondition(FuncT1<Boolean, ControllableCard> useCondition)
    {
        this.useCondition = useCondition;

        return this;
    }

    public void update()
    {
        if (onUpdate != null)
        {
            onUpdate.complete(this);
        }
    }

    public void update(ControllableCardPile pile)
    {
        card.update();
    }
}
