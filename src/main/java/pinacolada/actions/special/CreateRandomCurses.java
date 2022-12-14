package pinacolada.actions.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.random.Random;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameUtilities;

public class CreateRandomCurses extends PCLActionWithCallback<AbstractCard>
{
    protected final CardGroup destination;

    public CreateRandomCurses(int amount, CardGroup destination)
    {
        super(ActionType.CARD_MANIPULATION);

        this.destination = destination;

        initialize(amount);
    }

    public static AbstractCard getRandomCurse(Random rng)
    {
        return GameUtilities.getRandomCard(AbstractCard.CardRarity.CURSE, AbstractCard.CardType.CURSE).makeCopy();
    }

    @Override
    protected void firstUpdate()
    {
        final float speed = amount < 2 ? Settings.ACTION_DUR_FAST : amount < 3 ? Settings.ACTION_DUR_FASTER : Settings.ACTION_DUR_XFAST;
        for (int i = 0; i < amount; i++)
        {
            GameActions.top.makeCard(getRandomCurse(rng), destination)
                    .addCallback((ActionT1<AbstractCard>) this::complete)
                    .setDuration(speed, true);
        }

        complete();
    }
}
