package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.VisiblePower;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnClickableUsedSubscriber;
import pinacolada.powers.PCLClickableUse;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.PCLSubscribingPower;
import pinacolada.resources.PGR;

@VisiblePower
public class InnovationPower extends PCLSubscribingPower implements OnClickableUsedSubscriber {
    public static final PCLPowerData DATA = register(InnovationPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.SingleTurnNext)
            .setTooltip(PGR.core.tooltips.innovation);

    public InnovationPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public boolean onClickablePowerUsed(PCLClickableUse power, AbstractMonster target, int uses) {
        reducePower(1);
        this.flashWithoutSound();
        return false;
    }
}
