package pinacolada.powers.common;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisiblePower;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.TemporaryPower;
import pinacolada.resources.PGR;

@VisiblePower
public class BlindedPower extends PCLPower {
    public static final PCLPowerData DATA = register(BlindedPower.class)
            .setType(PowerType.DEBUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.TurnBased)
            .setTooltip(PGR.core.tooltips.blinded);

    public BlindedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return super.atDamageGive(type == DamageInfo.DamageType.NORMAL ? (damage - amount) : damage, type);
    }
}
