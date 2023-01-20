package pinacolada.actions.cardManipulation;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.utilities.GameUtilities;

import java.util.UUID;

// Copied and modified from STS-AnimatorMod
public class ModifyAllInstances extends PCLActionWithCallback<AbstractCard>
{
    protected final UUID uuid;
    protected boolean includeMasterDeck;

    public ModifyAllInstances(UUID targetUUID, ActionT1<AbstractCard> onCompletion)
    {
        this(targetUUID);

        addCallback(onCompletion);
    }

    public <S> ModifyAllInstances(UUID targetUUID, S state, ActionT2<S, AbstractCard> onCompletion)
    {
        this(targetUUID);

        addCallback(state, onCompletion);
    }

    public ModifyAllInstances(UUID targetUUID)
    {
        super(ActionType.CARD_MANIPULATION);

        this.uuid = targetUUID;

        initialize(1);
    }

    @Override
    protected void firstUpdate()
    {
        if (includeMasterDeck)
        {
            for (AbstractCard card : GameUtilities.getAllInstances(uuid))
            {
                complete(card);
            }
        }
        else
        {
            for (AbstractCard card : GameUtilities.getAllInBattleInstances(uuid))
            {
                complete(card);
            }
        }
    }

    public ModifyAllInstances includeMasterDeck(boolean includeMasterDeck)
    {
        this.includeMasterDeck = includeMasterDeck;

        return this;
    }
}
