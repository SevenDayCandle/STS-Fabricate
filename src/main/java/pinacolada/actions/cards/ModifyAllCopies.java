package pinacolada.actions.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import pinacolada.actions.PCLAction;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class ModifyAllCopies extends PCLAction<AbstractCard>
{
    protected final String cardID;
    protected boolean includeMasterDeck;

    public ModifyAllCopies(String cardID, ActionT1<AbstractCard> onCompletion)
    {
        this(cardID);

        addCallback(onCompletion);
    }

    public <S> ModifyAllCopies(String cardID, S state, ActionT2<S, AbstractCard> onCompletion)
    {
        this(cardID);

        addCallback(state, onCompletion);
    }

    public ModifyAllCopies(String cardID)
    {
        super(ActionType.CARD_MANIPULATION);

        this.cardID = cardID;

        initialize(1);
    }

    @Override
    protected void firstUpdate()
    {
        if (includeMasterDeck)
        {
            for (AbstractCard card : GameUtilities.getAllCopies(cardID))
            {
                complete(card);
            }
        }
        else
        {
            for (AbstractCard card : GameUtilities.getAllInBattleCopies(cardID))
            {
                complete(card);
            }
        }
    }

    public ModifyAllCopies includeMasterDeck(boolean includeMasterDeck)
    {
        this.includeMasterDeck = includeMasterDeck;

        return this;
    }
}
