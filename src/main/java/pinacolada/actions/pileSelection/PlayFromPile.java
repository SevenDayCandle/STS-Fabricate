package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.cardManipulation.PlayCard;
import pinacolada.actions.PCLActions;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PlayFromPile extends SelectFromPile
{
    protected ActionT1<PlayCard> onPlayCard;
    protected AbstractMonster target;

    public PlayFromPile(String sourceName, int amount, CardGroup... groups)
    {
        this(sourceName, null, amount, groups);
    }

    public PlayFromPile(String sourceName, AbstractMonster target, int amount, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, amount, groups);

        this.target = target;
    }

    @Override
    protected void addCard(CardGroup group, AbstractCard card)
    {
        super.addCard(group, card);

        card.calculateCardDamage(target);
    }

    @Override
    protected void complete()
    {
        super.complete();

        for (CardGroup group : groups)
        {
            if (group.type != CardGroup.CardGroupType.HAND)
            {
                for (AbstractCard card : group.group)
                {
                    card.resetAttributes();
                }
            }
        }
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        for (AbstractCard card : result)
        {
            PlayCard action = PCLActions.top.playCard(card, target)
                    .setSourcePile(GameUtilities.findCardGroup(card, false));

            if (onPlayCard != null)
            {
                onPlayCard.invoke(action);
            }
        }

        super.complete(result);
    }

    public PlayFromPile onPlayCard(ActionT1<PlayCard> onPlayCard)
    {
        this.onPlayCard = onPlayCard;

        return this;
    }

    @Override
    public String updateMessage()
    {
        return super.updateMessageInternal(CardRewardScreen.TEXT[0]);
    }
}
