package pinacolada.actions.cardManipulation;

import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import pinacolada.actions.PCLAction;
import pinacolada.utilities.GameActions;

public class ReshuffleDiscardPile extends PCLAction
{
    protected boolean onlyIfEmpty;

    public ReshuffleDiscardPile(boolean onlyIfEmpty)
    {
        super(ActionType.WAIT);

        this.onlyIfEmpty = onlyIfEmpty;

        initialize(1);
    }

    @Override
    protected void firstUpdate()
    {
        if (!onlyIfEmpty || player.drawPile.isEmpty())
        {
            GameActions.top.add(new EmptyDeckShuffleAction());
        }

        complete();
    }
}
