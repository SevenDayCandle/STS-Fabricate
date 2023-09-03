package pinacolada.powers.common;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisiblePower;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.TemporaryPower;
import pinacolada.resources.PGR;

@VisiblePower
public class BruisedPower extends PCLPower {
    public static final PCLPowerData DATA = register(BruisedPower.class)
            .setType(PowerType.DEBUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.TurnBased)
            .setTooltip(PGR.core.tooltips.bruised);

    public BruisedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        return super.atDamageReceive(type == DamageInfo.DamageType.NORMAL ? damage + amount : damage, type);
    }
}
