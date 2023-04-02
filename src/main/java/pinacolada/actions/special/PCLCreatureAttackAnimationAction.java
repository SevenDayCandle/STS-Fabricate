package pinacolada.actions.special;

import com.megacrit.cardcrawl.core.Settings;
import pinacolada.actions.PCLAction;
import pinacolada.monsters.PCLCreature;

public class PCLCreatureAttackAnimationAction extends PCLAction<Void>
{
    public final PCLCreature pSource;

    public PCLCreatureAttackAnimationAction(PCLCreature source)
    {
        super(ActionType.WAIT, Settings.ACTION_DUR_FAST);
        this.source = source;
        pSource = source;
    }

    public void firstUpdate()
    {
        super.firstUpdate();
        pSource.doActionAnimation();
    }
}
