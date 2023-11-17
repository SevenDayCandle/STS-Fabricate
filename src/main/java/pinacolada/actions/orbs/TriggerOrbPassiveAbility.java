package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.interfaces.subscribers.OnStartOfTurnPostDrawSubscriber;
import pinacolada.utilities.GameUtilities;

public class TriggerOrbPassiveAbility extends GenericOrbAction {
    public TriggerOrbPassiveAbility(int times) {
        this(times, 1, false, null);
    }

    public TriggerOrbPassiveAbility(AbstractOrb orb, int times) {
        this(times, 1, false, orb);
    }

    public TriggerOrbPassiveAbility(int times, int limit) {
        this(times, limit, false, null);
    }

    public TriggerOrbPassiveAbility(int times, int limit, boolean random, AbstractOrb orb) {
        super(times, limit, random, orb);
    }

    @Override
    protected void doEffectImpl(AbstractOrb orb, int am) {
        for (int i = 0; i < am; i++) {
            orb.onStartOfTurn();
            orb.onEndOfTurn();
            if (orb instanceof OnStartOfTurnPostDrawSubscriber) {
                ((OnStartOfTurnPostDrawSubscriber) orb).onStartOfTurnPostDraw();
            }
        }
    }
}
