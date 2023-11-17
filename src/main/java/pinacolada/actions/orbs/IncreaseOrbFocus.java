package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.utilities.GameUtilities;

public class IncreaseOrbFocus extends GenericOrbAction {
    public IncreaseOrbFocus(int times) {
        this(times, 1, false, null);
    }

    public IncreaseOrbFocus(AbstractOrb orb, int times) {
        this(times, 1, false, orb);
    }

    public IncreaseOrbFocus(int times, int limit) {
        this(times, limit, false, null);
    }

    public IncreaseOrbFocus(int times, int limit, boolean random, AbstractOrb orb) {
        super(times, limit, random, orb);
    }

    @Override
    protected void doEffectImpl(AbstractOrb orb, int am) {
        GameUtilities.modifyOrbBaseFocus(orb, am, true, false);
    }
}
