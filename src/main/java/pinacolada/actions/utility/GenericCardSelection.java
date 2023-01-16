package pinacolada.actions.utility;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.utilities.GenericCondition;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;

// TODO merge with SelectFromPile
public class GenericCardSelection extends PCLActionWithCallback<ArrayList<AbstractCard>>
{
    protected ArrayList<AbstractCard> result = new ArrayList<>();
    protected GenericCondition<AbstractCard> filter;
    protected ListSelection<AbstractCard> selection;
    protected AbstractCard card;
    protected CardGroup group;
    protected boolean forceSelect;

    protected GenericCardSelection(AbstractCard card, CardGroup group, int amount)
    {
        super(ActionType.CARD_MANIPULATION);

        this.card = card;
        this.group = group;

        initialize(amount);
    }

    public GenericCardSelection(CardGroup group, int amount)
    {
        this(null, group, amount);
    }

    public GenericCardSelection(AbstractCard card)
    {
        this(card, null, 1);
    }

    protected boolean canSelect(AbstractCard card)
    {
        return filter == null || filter.check(card);
    }

    @Override
    protected void firstUpdate()
    {
        if (card != null)
        {
            if (forceSelect || canSelect(card))
            {
                selectCard(card);
            }

            complete(result);
            return;
        }

        if (group == null)
        {
            complete(null);
            return;
        }

        final ArrayList<AbstractCard> list = new ArrayList<>();
        for (AbstractCard card : group.group)
        {
            if (forceSelect || canSelect(card))
            {
                list.add(card);
            }
        }

        if (selection == null)
        {
            selection = ListSelection.last(0);
        }

        selection.forEach(list, amount, this::selectCard);
        complete(result);
    }

    public GenericCardSelection forceSelect(boolean forceSelect)
    {
        this.forceSelect = forceSelect;

        return this;
    }

    protected void selectCard(AbstractCard card)
    {
        result.add(card);
    }

    public <S> GenericCardSelection setFilter(S state, FuncT2<Boolean, S, AbstractCard> filter)
    {
        this.filter = GenericCondition.fromT2(filter, state);

        return this;
    }

    public GenericCardSelection setFilter(FuncT1<Boolean, AbstractCard> filter)
    {
        this.filter = GenericCondition.fromT1(filter);

        return this;
    }

    public GenericCardSelection setSelection(ListSelection<AbstractCard> selection)
    {
        this.selection = selection;

        return this;
    }
}