package pinacolada.actions.utility;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.utilities.GenericCondition;
import pinacolada.actions.PCLAction;

import java.util.ArrayList;

public abstract class CardFilterAction extends PCLAction<ArrayList<AbstractCard>>
{
    protected final ArrayList<AbstractCard> selectedCards = new ArrayList<>();
    protected boolean canPlayerCancel;
    protected GenericCondition<ArrayList<AbstractCard>> condition;
    protected GenericCondition<AbstractCard> filter;
    protected FuncT1<String, ArrayList<AbstractCard>> dynamicString;
    protected ActionT3<CardGroup, ArrayList<AbstractCard>, AbstractCard> onClickCard;

    public CardFilterAction(ActionType type)
    {
        super(type);
    }

    public CardFilterAction(ActionType type, float duration)
    {
        super(type, duration);
    }

    protected boolean canSelect(AbstractCard card)
    {
        return filter == null || filter.check(card);
    }

    public CardFilterAction cancellableFromPlayer(boolean value)
    {
        this.canPlayerCancel = value;

        return this;
    }

    public CardFilterAction setCompletionRequirement(FuncT1<Boolean, ArrayList<AbstractCard>> condition)
    {
        this.condition = GenericCondition.fromT1(condition);

        return this;
    }

    public CardFilterAction setDynamicMessage(FuncT1<String, ArrayList<AbstractCard>> stringFunc)
    {
        this.dynamicString = stringFunc;

        return this;
    }

    public CardFilterAction setFilter(FuncT1<Boolean, AbstractCard> filter)
    {
        this.filter = GenericCondition.fromT1(filter);

        return this;
    }

    public <S> CardFilterAction setFilter(S state, FuncT2<Boolean, S, AbstractCard> filter)
    {
        this.filter = GenericCondition.fromT2(filter, state);

        return this;
    }

    public CardFilterAction setOnClick(ActionT3<CardGroup, ArrayList<AbstractCard>, AbstractCard> onClickCard)
    {
        this.onClickCard = onClickCard;

        return this;
    }
}
