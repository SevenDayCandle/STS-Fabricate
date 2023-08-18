package pinacolada.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.cards.base.PCLCardData;
import pinacolada.interfaces.subscribers.PCLCombatSubscriber;
import pinacolada.skills.PSkill;

public class PSpecialCardPower extends PCLClickablePower implements PCLCombatSubscriber {
    protected PSkill<?> move;

    public PSpecialCardPower(PCLCardData data, AbstractCreature owner, PSkill<?> move) {
        super(owner, data);
        this.move = move;
    }

    @Override
    public String getUpdatedDescription() {
        return move != null ? move.getPowerText() : "";
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
