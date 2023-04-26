package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnOrbChannelSubscriber;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameUtilities;

public class SorceryPower extends PCLPower implements OnOrbChannelSubscriber {
    public static final String POWER_ID = createFullID(SorceryPower.class);

    public SorceryPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        initialize(amount);
    }

    public float modifyOrbOutgoing(float initial) {
        return initial + amount;
    }

    @Override
    public void onRemove() {
        super.onRemove();

        CombatManager.unsubscribe(this);
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();

        CombatManager.subscribe(this);
    }

    @Override
    public void onChannelOrb(AbstractOrb orb) {
        GameUtilities.modifyOrbBaseFocus(orb, amount, true, false);
        removePower();
    }
}
