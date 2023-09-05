package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.annotations.VisiblePower;
import pinacolada.interfaces.subscribers.OnTryApplyPowerSubscriber;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.PCLSubscribingPower;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisiblePower
public class SilencedPower extends PCLSubscribingPower implements OnTryApplyPowerSubscriber {
    public static final PCLPowerData DATA = register(SilencedPower.class)
            .setType(PowerType.DEBUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.TurnBased)
            .setPriority(99)
            .setTooltip(PGR.core.tooltips.silenced);

    public SilencedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public boolean tryApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source, AbstractGameAction action) {
        return !GameUtilities.isPCLBuff(power) || (power.owner != owner && target != owner);
    }
}