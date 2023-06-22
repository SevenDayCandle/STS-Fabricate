package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnClickableUsedSubscriber;
import pinacolada.powers.PCLClickableUse;
import pinacolada.powers.PCLPower;

public class InnovationPower extends PCLPower implements OnClickableUsedSubscriber {
    public static final String POWER_ID = createFullID(InnovationPower.class);

    public InnovationPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        initialize(amount);
    }

    @Override
    public boolean onClickablePowerUsed(PCLClickableUse power, AbstractMonster target, int uses) {
        reducePower(1);
        this.flashWithoutSound();
        return false;
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
}
