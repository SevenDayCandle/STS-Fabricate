package pinacolada.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.cards.base.PCLCardData;
import pinacolada.interfaces.subscribers.PCLCombatSubscriber;
import pinacolada.relics.PCLRelic;

public class PCLSubscribingPower extends PCLPower implements PCLCombatSubscriber {
    protected PCLSubscribingPower(AbstractCreature owner, AbstractCreature source) {
        super(owner, source);
    }

    public PCLSubscribingPower(AbstractCreature owner, PCLRelic relic) {
        super(owner, relic);
    }

    public PCLSubscribingPower(AbstractCreature owner, AbstractCreature source, PCLRelic relic) {
        super(owner, source, relic);
    }

    public PCLSubscribingPower(AbstractCreature owner, PCLCardData cardData) {
        super(owner, cardData);
    }

    public PCLSubscribingPower(AbstractCreature owner, AbstractCreature source, PCLCardData cardData) {
        super(owner, source, cardData);
    }

    public PCLSubscribingPower(AbstractCreature owner, String id) {
        super(owner, id);
    }

    public PCLSubscribingPower(AbstractCreature owner, AbstractCreature source, String id) {
        super(owner, source, id);
    }

    public void onRemove() {
        super.onRemove();
        unsubscribeFromAll();
    }

    public void onInitialApplication() {
        super.onInitialApplication();
        powerSubscribeTo();
    }

    // Override this if you do not want automatic subscription on your power
    public void powerSubscribeTo() {
        subscribeToAll();
    }
}
