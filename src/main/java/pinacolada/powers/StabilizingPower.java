package pinacolada.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.interfaces.subscribers.OnTryReducePowerSubscriber;
import pinacolada.resources.PGR;

public class StabilizingPower extends PCLSubscribingPower implements InvisiblePower, OnTryReducePowerSubscriber {
    public static final String POWER_ID = PGR.core.createID(StabilizingPower.class.getSimpleName());
    protected AbstractPower target;

    public StabilizingPower(AbstractCreature owner, AbstractPower target) {
        super(owner, POWER_ID);
        this.target = target;
    }

    public void atStartOfTurnPostDraw() {
        super.atStartOfTurnPostDraw();
        removePower();
    }

    @Override
    public boolean tryReducePower(AbstractPower power, AbstractCreature a, AbstractCreature b, AbstractGameAction c) {
        return power == null || !target.ID.equals(power.ID);
    }
}
