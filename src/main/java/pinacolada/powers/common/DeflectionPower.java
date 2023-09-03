package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisiblePower;
import pinacolada.effects.PCLSFX;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;

@VisiblePower
public class DeflectionPower extends PCLPower {
    public static final PCLPowerData DATA = register(DeflectionPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.TurnBased)
            .setTooltip(PGR.core.tooltips.deflection);

    public DeflectionPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        int blockedAmount = info.output - damageAmount;
        if (info.type == DamageInfo.DamageType.NORMAL && blockedAmount > 0 && info.owner != null) {
            PCLActions.bottom.dealDamage(owner, info.owner, blockedAmount, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        }

        return super.onAttacked(info, damageAmount);
    }

    @Override
    public void playApplyPowerSfx() {
        PCLSFX.play(PCLSFX.ATTACK_IRON_1, 1.25f, 1.35f, 0.7f);
    }
}