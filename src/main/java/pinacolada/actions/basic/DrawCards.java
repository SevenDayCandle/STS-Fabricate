package pinacolada.actions.basic;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.NoDrawPower;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.utilities.GenericCondition;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.utilities.GameActions;

import java.util.ArrayList;

public class DrawCards extends PCLActionWithCallback<ArrayList<AbstractCard>>
{
    protected final ArrayList<AbstractCard> cards = new ArrayList<>();
    protected GenericCondition<AbstractCard> filter;
    protected boolean canDrawUnfiltered;
    protected boolean shuffleIfEmpty = true;

    protected DrawCards(DrawCards other, int amount)
    {
        this(amount);

        shuffleIfEmpty = other.shuffleIfEmpty;
        filter = other.filter;
        canDrawUnfiltered = other.canDrawUnfiltered;
        callbacks.addAll(other.callbacks);
        cards.addAll(other.cards);
    }

    public DrawCards(int amount)
    {
        super(ActionType.DRAW);

        initialize(amount);
    }

    @Override
    protected void firstUpdate()
    {
        if (player.hasPower(NoDrawPower.POWER_ID))
        {
            player.getPower(NoDrawPower.POWER_ID).flash();
            complete(cards);
            return;
        }

        if (amount <= 0)
        {
            complete(cards);
            return;
        }

        if (player.drawPile.isEmpty())
        {
            if (shuffleIfEmpty && !player.discardPile.isEmpty())
            {
                GameActions.top.sequential(
                        new EmptyDeckShuffleAction(),
                        new DrawCards(this, amount)
                );

                complete(); // Do not trigger callback
            }
            else
            {
                complete(cards);
            }
        }
        else if (player.hand.size() >= BaseMod.MAX_HAND_SIZE)
        {
            player.createHandIsFullDialog();
            complete(cards);
        }
        else
        {
            if (filter != null)
            {
                AbstractCard filtered = null;
                for (AbstractCard card : player.drawPile.group)
                {
                    if (filter.check(card))
                    {
                        filtered = card;
                        break;
                    }
                }

                if (filtered != null)
                {
                    player.drawPile.removeCard(filtered);
                    player.drawPile.addToTop(filtered);
                }
                else if (!canDrawUnfiltered)
                {
                    complete(cards);
                    return;
                }
            }

            cards.add(player.drawPile.getTopCard());

            GameActions.top.sequential(
                    new DrawCardAction(source, 1, false),
                    new DrawCards(this, amount - 1)
            );

            complete(); // Do not trigger callback
        }
    }

    public <S> DrawCards setFilter(S state, FuncT2<Boolean, S, AbstractCard> filter, boolean canDrawUnfiltered)
    {
        this.filter = GenericCondition.fromT2(filter, state);
        this.canDrawUnfiltered = canDrawUnfiltered;

        return this;
    }

    public DrawCards setFilter(FuncT1<Boolean, AbstractCard> filter, boolean canDrawUnfiltered)
    {
        this.filter = GenericCondition.fromT1(filter);
        this.canDrawUnfiltered = canDrawUnfiltered;

        return this;
    }

    public DrawCards shuffleIfEmpty(boolean shuffleIfEmpty)
    {
        this.shuffleIfEmpty = shuffleIfEmpty;

        return this;
    }
}
