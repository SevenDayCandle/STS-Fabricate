package pinacolada.actions.special;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import pinacolada.actions.PCLAction;
import pinacolada.utilities.GameUtilities;

public class ObtainRelicAction extends PCLAction<AbstractRelic> {
    protected final AbstractRelic relic;

    public ObtainRelicAction(AbstractRelic relic) {
        super(ActionType.SPECIAL);

        this.relic = relic;
        this.isRealtime = true;
        this.canCancel = false;

        initialize(1);
    }

    @Override
    protected void firstUpdate() {
        if (relic == null) {
            complete(null);
            return;
        }
        GameUtilities.obtainRelicFromEvent(relic);
        complete(relic);
    }
}
