package pinacolada.dungeon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.utilities.GenericCallback;
import pinacolada.ui.combat.ControllableCardPile;

public class CardController
{
    public final AbstractCard card;
    public ControllableCardPile sourcePile;
    protected boolean enabled;
    protected FuncT1<Boolean, CardController> useCondition;
    protected GenericCallback<CardController> onUpdate;
    protected GenericCallback<CardController> onSelect;
    protected GenericCallback<CardController> onDelete;

    public CardController(ControllableCardPile sourcePile, AbstractCard card)
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

    public <S> CardController onDelete(S state, ActionT2<S, CardController> onCompletion)
    {
        onDelete = GenericCallback.fromT2(onCompletion, state);

        return this;
    }

    public CardController onDelete(ActionT1<CardController> onCompletion)
    {
        onDelete = GenericCallback.fromT1(onCompletion);

        return this;
    }

    public <S> CardController onSelect(S state, ActionT2<S, CardController> onCompletion)
    {
        onSelect = GenericCallback.fromT2(onCompletion, state);

        return this;
    }

    public CardController onSelect(ActionT1<CardController> onCompletion)
    {
        onSelect = GenericCallback.fromT1(onCompletion);

        return this;
    }

    public <S> CardController onUpdate(S state, ActionT2<S, CardController> onCompletion)
    {
        onUpdate = GenericCallback.fromT2(onCompletion, state);

        return this;
    }

    public CardController onUpdate(ActionT1<CardController> onCompletion)
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

    public CardController setUseCondition(FuncT1<Boolean, CardController> useCondition)
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
