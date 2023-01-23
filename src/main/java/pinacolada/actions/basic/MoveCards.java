package pinacolada.actions.basic;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.utilities.GenericCondition;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class MoveCards extends PCLActionWithCallback<ArrayList<AbstractCard>>
{
    protected ArrayList<AbstractCard> selectedCards = new ArrayList<>();
    protected GenericCondition<AbstractCard> filter;
    protected ListSelection<AbstractCard> destination;
    protected ListSelection<AbstractCard> origin;
    protected CardGroup targetPile;
    protected CardGroup sourcePile;
    protected boolean showEffect;
    protected boolean realtime;
    protected float effectDuration;

    public MoveCards(CardGroup targetPile, CardGroup sourcePile)
    {
        this(targetPile, sourcePile, -1);
    }

    public MoveCards(CardGroup targetPile, CardGroup sourcePile, int amount)
    {
        super(ActionType.CARD_MANIPULATION);

        this.destination = null;
        this.origin = PCLCardSelection.Top.toSelection();
        this.targetPile = targetPile;
        this.sourcePile = sourcePile;

        initialize(amount);
    }

    @Override
    protected void firstUpdate()
    {
        ArrayList<AbstractCard> temp = new ArrayList<>();
        for (AbstractCard card : sourcePile.group)
        {
            if (filter == null || filter.check(card))
            {
                temp.add(card);
            }
        }

        int max = amount;
        if (amount == -1 || temp.size() < amount)
        {
            max = temp.size();
        }

        boolean remove = origin.mode.isRandom();
        for (int i = 0; i < max; i++)
        {
            final AbstractCard card = origin.get(temp, i, remove);
            if (card != null)
            {
                moveCard(card);
            }
        }

        complete(selectedCards);
    }

    private void moveCard(AbstractCard card)
    {
        selectedCards.add(card);
        PCLActions.top.moveCard(card, sourcePile, targetPile)
                .showEffect(showEffect, realtime, effectDuration)
                .setDestination(destination);
    }

    public MoveCards setDestination(ListSelection<AbstractCard> destination)
    {
        this.destination = destination;

        return this;
    }

    public MoveCards setFilter(FuncT1<Boolean, AbstractCard> filter)
    {
        this.filter = GenericCondition.fromT1(filter);

        return this;
    }

    public <S> MoveCards setFilter(S state, FuncT2<Boolean, S, AbstractCard> filter)
    {
        this.filter = GenericCondition.fromT2(filter, state);

        return this;
    }

    public MoveCards setOrigin(ListSelection<AbstractCard> origin)
    {
        this.origin = (origin != null ? origin : PCLCardSelection.Top.toSelection());

        return this;
    }

    public MoveCards showEffect(boolean showEffect, boolean isRealtime)
    {
        float duration = showEffect ? Settings.ACTION_DUR_MED : Settings.ACTION_DUR_FAST;

        if (Settings.FAST_MODE)
        {
            duration *= 0.7f;
        }

        return showEffect(showEffect, isRealtime, duration);
    }

    public MoveCards showEffect(boolean showEffect, boolean isRealtime, float effectDuration)
    {
        this.showEffect = showEffect;
        this.realtime = isRealtime;
        this.effectDuration = effectDuration;

        return this;
    }
}
