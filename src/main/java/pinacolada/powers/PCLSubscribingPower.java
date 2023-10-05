package pinacolada.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.interfaces.subscribers.PCLCombatSubscriber;

public class PCLSubscribingPower extends PCLPower implements PCLCombatSubscriber {

    public PCLSubscribingPower(PCLPowerData data, AbstractCreature owner, AbstractCreature source, int amount) {
        super(data, owner, source, amount);
    }

    public void onDeath() {
        super.onDeath();
        unsubscribeFromAll();
    }

    public void onInitialApplication() {
        super.onInitialApplication();
        powerSubscribeTo();
    }

    public void onRemove() {
        super.onRemove();
        unsubscribeFromAll();
    }

    // Override this if you do not want automatic subscription on your power
    public void powerSubscribeTo() {
        subscribeToAll();
    }
}
