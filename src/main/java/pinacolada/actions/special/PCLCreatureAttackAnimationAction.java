package pinacolada.actions.special;

import com.megacrit.cardcrawl.core.Settings;
import pinacolada.actions.PCLAction;
import pinacolada.monsters.PCLCreature;

public class PCLCreatureAttackAnimationAction extends PCLAction<Void> {
    public final PCLCreature pSource;
    public final boolean setTakenTurn;

    public PCLCreatureAttackAnimationAction(PCLCreature source, boolean setTakenTurn) {
        super(ActionType.WAIT, Settings.ACTION_DUR_FAST);
        this.source = source;
        this.pSource = source;
        this.setTakenTurn = setTakenTurn;
    }

    public void firstUpdate() {
        super.firstUpdate();
        pSource.doActionAnimation(setTakenTurn);
    }
}
