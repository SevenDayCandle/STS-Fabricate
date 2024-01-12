package pinacolada.actions.special;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.actions.PCLAction;
import pinacolada.utilities.GameUtilities;

public class ObtainBlightAction extends PCLAction<AbstractBlight> {
    protected final AbstractBlight relic;

    public ObtainBlightAction(AbstractBlight relic) {
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
        GameUtilities.obtainBlight(Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f, relic);
        complete(relic);
    }
}
