package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.utilities.GameUtilities;

public class IncreaseOrbEvoke extends GenericOrbAction {
    public IncreaseOrbEvoke(int times) {
        this(times, 1, false, null);
    }

    public IncreaseOrbEvoke(AbstractOrb orb, int times) {
        this(times, 1, false, orb);
    }

    public IncreaseOrbEvoke(int times, int limit) {
        this(times, limit, false, null);
    }

    public IncreaseOrbEvoke(int times, int limit, boolean random, AbstractOrb orb) {
        super(times, limit, random, orb);
    }

    @Override
    protected void doEffectImpl(AbstractOrb orb, int am) {
        orb.evokeAmount += am;
        GameUtilities.modifyOrbBaseEvokeAmount(orb, am, true, true);
    }
}
