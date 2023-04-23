package pinacolada.actions.special;

import com.megacrit.cardcrawl.core.Settings;
import pinacolada.actions.PCLAction;
import pinacolada.monsters.PCLCreature;

public class PCLCreatureAttackAnimationAction extends PCLAction<Void>
{
    public final PCLCreature pSource;
    public final boolean takenTurn;

    public PCLCreatureAttackAnimationAction(PCLCreature source, boolean takenTurn)
    {
        super(ActionType.WAIT, Settings.ACTION_DUR_FAST);
        this.source = source;
        this.pSource = source;
        this.takenTurn = takenTurn;
    }

    public void firstUpdate()
    {
        super.firstUpdate();
        pSource.doActionAnimation(takenTurn);
    }
}
