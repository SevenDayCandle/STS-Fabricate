package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.actions.PCLAction;

public class RemoveOrb extends PCLAction
{
    private final AbstractOrb orb;

    public RemoveOrb(AbstractOrb orb)
    {
        super(ActionType.SPECIAL);

        this.orb = orb;
    }

    @Override
    protected void firstUpdate()
    {
        if (player.orbs.remove(orb))
        {
            player.orbs.add(0, orb);
            player.removeNextOrb();
        }

        complete();
    }
}
