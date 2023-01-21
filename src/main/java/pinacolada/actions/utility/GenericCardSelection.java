package pinacolada.actions.utility;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.actions.CardFilterAction;

// Copied and modified from STS-AnimatorMod
public class GenericCardSelection extends CardFilterAction
{
    protected GenericCardSelection(AbstractCard card, int amount)
    {
        super(ActionType.CARD_MANIPULATION);

        this.card = card;

        initialize(amount);
    }

    public GenericCardSelection(AbstractCard card)
    {
        this(card, 1);
    }

    @Override
    protected void firstUpdate()
    {
        if (card != null)
        {
            if (!canPlayerCancel || canSelect(card))
            {
                selectCard(card);
            }
            complete(selectedCards);
        }
    }


    protected void selectCard(AbstractCard card)
    {
        selectedCards.add(card);
    }
}